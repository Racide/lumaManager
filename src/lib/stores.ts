import type { Profile, SteamApp } from "./types";
import { writable } from 'svelte/store';
import * as data from "./loadData";

export const profiles = (() => {
    const { subscribe, update } = writable(data.profiles);

    return {
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
)();

export const profile = (() => {
    const { subscribe, set, update } = writable(data.profile);

    return {
        subscribe,
        set,
        addSteamApp: (game: SteamApp) => update(p => {
            p.steamApps.push(game);
            return p;
        })
    };
})();
