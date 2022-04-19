import {
  Profile,
  type Profiles,
  SteamApp,
  type ReadonlySteamApps,
  type LumaManagerDB,
  type JSONSteamApps,
  newSteamApps,
  newProfiles,
  dBVersion,
  steamAppsUrl,
  profileDB,
  defaultProfileName,
  steamAppsDB,
  profilesDB,
  dBName,
} from "./types";
import * as http from "@tauri-apps/api/http";
import { openDB, type IDBPDatabase } from "idb";
import Fuse from "fuse.js";
import { writable, type Writable } from "svelte/store";

let db: IDBPDatabase<LumaManagerDB>;
export let steamApps: ReadonlySteamApps;
let profiles: Profiles;
let profile: Profile;
let profilesStore: {
  subscribe: Writable<Profiles>["subscribe"],
  addProfile: (profile: Profile) => void,
  delProfile: (id: Profile["id"]) => void;
};
let profileStore: {
  subscribe: Writable<Profile>["subscribe"],
  set: Writable<Profile>["set"],
  addSteamApp: (game: SteamApp) => void;
};
export {
  profilesStore as profiles,
  profileStore as profile
};
export let fuse: Fuse<SteamApp>;
const fuseOptions: Fuse.IFuseOptions<SteamApp> = {
  isCaseSensitive: false,
  // includeScore: false,
  shouldSort: true,
  // includeMatches: false,
  // findAllMatches: false,
  // minMatchCharLength: 1,
  // location: 0,
  // threshold: 0.6,
  // distance: 100,
  // useExtendedSearch: false,
  // ignoreLocation: false,
  // ignoreFieldNorm: false,
  // fieldNormWeight: 1,
  keys: ["title"],
};

async function syncSteamApps() {
  const response = await http.fetch<JSONSteamApps>(
    steamAppsUrl
  );
  if (!response) {
    throw Error("todo error");
  }
  steamApps = newSteamApps(response.data);
  const tx = db.transaction(steamAppsDB, "readwrite");
  await Promise.all(
    [
      ...steamApps.map((steamApp) => tx.store.put(steamApp.toStorage())),
      tx.done
    ]
  );
}

export async function loadData() {
  db = await openDB<LumaManagerDB>(dBName, dBVersion, {
    upgrade(db, oldVersion) {
      switch (oldVersion) {
        case 0:
          db.createObjectStore(steamAppsDB, {
            keyPath: 'id',
          });
          const profilesStore = db.createObjectStore(profilesDB, {
            keyPath: "id",
          });
          const activeProfileStore = db.createObjectStore(profileDB);
          profilesStore.put(new Profile(defaultProfileName).toStorage());
          activeProfileStore.put(defaultProfileName, profileDB);
          console.log("upgrade done");
      }
    },
  });
  console.log("loading steamApps");
  let error;
  try {
    await syncSteamApps();
    console.log("steamApps loaded");
  } catch (ex) {
    console.log(ex);
    if (await db.count(steamAppsDB) == 0) {
      error = Error("todo fatal, no steamapps", { cause: ex });
    } else {
      error = Error("todo offline", { cause: ex });
    }
  }
  profiles = newProfiles(await db.getAll(profilesDB), steamApps);
  profile = profiles.get((await db.get(profileDB, profileDB))!)!;
  fuse = new Fuse(steamApps, fuseOptions);

  {
    const { subscribe, update } = writable(profiles);

    profilesStore = {
      subscribe,
      addProfile: (profile: Profile) => update(profiles => {
        profiles.push(profile);
        return profiles;
      }),
      delProfile: (id: Profile["id"]) => update(profiles => {
        profiles.del(id);
        return profiles;
      })
    };
  }

  {
    const { subscribe, set, update } = writable(profile);

    profileStore = {
      subscribe,
      set,
      addSteamApp: (game: SteamApp) => update(p => {
        p.steamApps.push(game);
        return p;
      })
    };
  }
}
