import { Profile, type Profiles, SteamApp, type JSONSteamAppInfo, type ReadonlySteamApps, type LumaManagerDB, type JSONSteamApps, newSteamApps, type DBProfile, newProfiles, DB_VERSION, steamAppsUrl } from "./types";
import * as http from "@tauri-apps/api/http";
import { openDB, type IDBPDatabase } from "idb";

let db: IDBPDatabase<LumaManagerDB>;
export let steamApps: ReadonlySteamApps;
export let profiles: Profiles;
export let profile: Profile;

async function syncSteamApps() {
  const response = await http.fetch<JSONSteamApps>(
    steamAppsUrl
  );
  if (!response) {
    throw Error("todo error");
  }
  steamApps = newSteamApps(response.data);
  const tx = db.transaction("steamApps", "readwrite");
  await Promise.all(
    [
      ...steamApps.map((steamApp) => tx.store.add(steamApp)),
      tx.done
    ]
  );
}

export async function loadData() {
  db = await openDB<LumaManagerDB>("SteamApps", 1, {
    async upgrade(db, oldVersion, _, tx) {
      switch (oldVersion) {
        case 0:
          // transaction 1
          db.createObjectStore("steamApps", {
            keyPath: 'id',
          });
          db.createObjectStore("profiles", {
            keyPath: "id",
          });
          db.createObjectStore("activeProfile");
          await tx.done;
          // transaction 2
          await db.add("profiles", new Profile("default"));
          await db.put("activeProfile", "deault", "activeProfile");
          console.log("upgrade done");
      }
    },
  });
  console.log("loading steamApps");
  let error = null;
  try {
    await syncSteamApps();
  } catch (ex) {
    console.log(ex);
    if (await db.count("steamApps") == 0) {
      error = Error("todo fatal, no steamapps", { cause: ex });
    } else {
      error = Error("todo offline", { cause: ex });
    }
  }
  profiles = newProfiles(await db.getAll("profiles"), steamApps);
  profile = profiles.get((await db.get("activeProfile", "activeProfile"))!)!;
}
