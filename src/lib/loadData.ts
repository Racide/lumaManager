import { Profile, Profiles, type SteamApp, type SteamData } from "./types";

type JSONSteamData = {
    readonly applist: {
        readonly apps: {
            readonly appid: number;
            readonly name: string;
        }[];
    };
};
type JSONProfiles = Record<string, { readonly steamApps: JSONSteamId[]; }>;
type JSONSteamId = number;

function newSteamData(jsonSteamData: JSONSteamData): SteamData {
    const steamData = new Array() as Array<SteamApp> & SteamData,
        index = new Map<number, SteamApp>();
    steamData.get = (id) => {
        return index.get(id);
    };
    steamData.has = (id) => {
        return index.has(id);
    };
    jsonSteamData.applist.apps.forEach((jsonSteamApp) => {
        const steamApp: SteamApp = {
            id: jsonSteamApp.appid,
            title: jsonSteamApp.name,
        };
        index.set(steamApp.id, steamApp);
        steamData.push(steamApp);
    });
    return steamData;
}

// Mockups
const jsonSteamData: JSONSteamData = await fetch("/assets/steamapps.json")
    .then((response) => response.json()).catch(() => {
        console.log("fuck");
    });

export const steamData: SteamData = newSteamData(jsonSteamData);

// Mockups
const dProfiles: JSONProfiles = {
    "bob games": {
        steamApps: (() => {
            const p = Math.floor(Math.random() * steamData.length),
                q = Math.floor(Math.random() * 100);
            return steamData.slice(p, Math.min(p + q, steamData.length)).map(e => e.id);
        })()
    },
    "fish games": {
        steamApps: (() => {
            const p = Math.floor(Math.random() * steamData.length),
                q = Math.floor(Math.random() * 100);
            return steamData.slice(p, Math.min(p + q, steamData.length)).map(e => e.id);
        })()
    },
    "empty": {
        steamApps: []
    }
};
const dProfile = Object.keys(dProfiles)[0];

export const profiles = ((jsonProfiles: JSONProfiles) => Object.keys(jsonProfiles).reduce((acc, key) => {
    acc.push(new Profile(
        key,
        jsonProfiles[key].steamApps.sort().map((steamId) => steamData.get(steamId)!)
    ));
    return acc;
}, new Profiles()))(dProfiles);

export let profile: Profile = profiles.get(dProfile)!;
