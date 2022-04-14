import type { DBSchema } from "idb";

export const DB_VERSION = 1;
export const steamAppsUrl = "https://api.steampowered.com/ISteamApps/GetAppList/v2/";

export interface LumaManagerDB extends DBSchema {
    steamApps: {
        key: number;
        value: SteamApp;
    };
    profiles: {
        key: string,
        value: DBProfile;
    };
    activeProfile: {
        key: string,
        value: string;
    };
}

type DB<Type> = { readonly [Key in keyof Omit<Type, "toJSON"> as (Type[Key] extends Function | Symbol ? never : Key)]: DB<Type[Key]> };

interface Serializable<Type> {
    toJSON: () => DB<Type>;
};

export type DBProfile = Omit<DB<Profile>, "text">;

export interface JSONSteamAppInfo {
    [key: number]: {
        readonly data: {
            readonly type: string;
        };
    };
}

export type JSONSteamApps = {
    readonly applist: {
        readonly apps: {
            readonly appid: number;
            readonly name: string;
            readonly type: string | undefined;
        }[];
    };
};

export class SteamApp implements Serializable<SteamApp> {
    private type: SteamApp.Type | undefined;

    constructor(
        public readonly id: number,
        public readonly title: string,
        type?: SteamApp.Type) {
        this.type = type;
    }
    toJSON() {
        return {
            id: this.id,
            title: this.title,
        };
    }

    getType() {
        return this.type;
    }

    setType(type: SteamApp.Type) {
        if (!this.type) {
            this.type = type;
        }
    }

};

namespace SteamApp {
    export type Type = string;//"APP" | "DLC";
}

interface SteamAppsExtra {
    get: Map<SteamApp["id"], SteamApp>["get"];
    has: Map<SteamApp["id"], SteamApp>["has"];
    setType: (id: SteamApp["id"], type: SteamApp.Type) => void;
    push: Array<SteamApp>["push"];
    del: (...id: SteamApp["id"][]) => void;
}
export type SteamApps = Array<SteamApp> & SteamAppsExtra;

export type ReadonlySteamApps = ReadonlyArray<SteamApp> & Pick<SteamApps, "get" | "has" | "setType">;

export class Profile {
    readonly id: string;
    readonly text: string;
    steamApps: SteamApps;

    constructor(id: Profile["id"], steamApps: SteamApps = newSteamApps()) {
        id = id.trim();
        this.id = id;
        this.text = id;
        this.steamApps = steamApps;
    }
};

interface ProfilesExtra {
    get: Map<Profile["id"], Profile>["get"];
    has: Map<Profile["id"], Profile>["has"];
    push: Array<Profile>["push"];
    del: (...ids: Profile["id"][]) => void;
    // toDBProfiles: () => DBProfiles;
}
export type Profiles = Array<Profile> & ProfilesExtra;

// constructors

export function newSteamApps(): SteamApps;
export function newSteamApps(jsonSteamApps: JSONSteamApps): ReadonlySteamApps;
export function newSteamApps(jsonSteamApps?: JSONSteamApps) {
    const array = new Array<SteamApp>(),
        index = new Map<SteamApp["id"], SteamApp>(),
        extra: SteamAppsExtra = {
            get(id) {
                return index.get(id);
            },
            has(id) {
                return index.has(id);
            },
            setType(id, type) {
                index.get(id)?.setType(type);
            },
            push(...steamApps) {
                for (const steamApp of steamApps) {
                    if (index.has(steamApp.id)) {
                        array.splice(steamApps.findIndex((e) => e.id == steamApp.id), 1, steamApp);
                    } else {
                        array.push(steamApp);
                    }
                    index.set(steamApp.id, steamApp);
                }
                return array.length;
            },
            del(...ids) {
                for (const id of ids) {
                    array.splice(array.indexOf(index.get(id)!), 1);
                    index.delete(id);
                }
            },
        };
    if (jsonSteamApps) {
        jsonSteamApps.applist.apps.forEach((jsonSteamApp) => {
            const steamApp = new SteamApp(
                jsonSteamApp.appid,
                jsonSteamApp.name,
                jsonSteamApp.type
            );
            index.set(steamApp.id, steamApp);
            array.push(steamApp);
        });
    }
    return Object.assign(array, extra);
}

export function newProfiles(dbProfiles: DBProfile[], steamApps: ReadonlySteamApps) {
    const array = new Array<Profile>(),
        index = new Map<Profile["id"], Profile>(),
        extra: ProfilesExtra = {
            get(id) {
                return index.get(id);
            },
            has(id) {
                return index.has(id);
            },
            push(...profiles) {
                for (const profile of profiles) {
                    if (index.has(profile.id)) {
                        array.splice(array.findIndex((e) => e.id == profile.id), 1, profile);
                    } else {
                        array.push(profile);
                    }
                    index.set(profile.id, profile);
                }
                return profiles.length;
            },
            del(...ids) {
                for (const id of ids) {
                    array.splice(array.indexOf(index.get(id)!), 1);
                    index.delete(id);
                }
            },
            // toDBProfiles() {
            //     array
            //     const dbProfiles: DBProfiles = {};
            //     for (const profile of array) {
            //         dbProfiles[profile.id] = { steamApps: profile.steamApps };
            //     }
            //     return dbProfiles;
            // }
        };

    for (const dbProfile of dbProfiles) {
        array.push(new Profile(
            dbProfile.id,
            dbProfile.steamApps.reduce(
                (acc, steamApp) => {
                    if (!steamApp.getType()) {
                        const type = steamApps.get(steamApp.id)?.getType();
                        if (type) {
                            steamApp.setType(type);
                        }
                    }
                    acc.push(steamApp);
                    return acc;
                },
                newSteamApps())
        ));
    }

    return Object.assign(array, extra);
}
