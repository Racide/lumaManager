import type { DBSchema } from "idb";

export const dBName = "lumaManager";
export const dBVersion = 1;
export const steamAppsUrl = "https://api.steampowered.com/ISteamApps/GetAppList/v2/";
export const steamAppsDB = "steamApps";
export const profilesDB = "profiles";
export const profileDB = "profile";
export const defaultProfileName = "default";

interface Storable<StorageType> {
    toStorage: () => Readonly<StorageType>;
};

type StorageType<Type> = Type extends Storable<infer StorageType> ? Readonly<StorageType> : never;

type Storagify<Type> = {
    readonly [Key in keyof Type as Type[Key] extends Function | Symbol ? never : Key]
    : Type[Key] extends Storable<infer StorageType> ? StorageType : Storagify<Type[Key]>
};

export interface LumaManagerDB extends DBSchema {
    steamApps: {
        key: StorageType<SteamApp>["id"];
        value: StorageType<SteamApp>;
    };
    profiles: {
        key: StorageType<Profile>["id"],
        value: StorageType<Profile>;
    };
    profile: {
        key: StorageType<Profile>["id"],
        value: StorageType<Profile>["id"];
    };
}

export class SteamApp implements Storable<{
    id: SteamApp["id"],
    title: SteamApp["title"],
    type: SteamApp["type"];
}> {
    private type: SteamApp.Type | undefined;

    constructor(
        public readonly id: number,
        public readonly title: string,
        type?: SteamApp.Type) {
        this.type = type;
    }

    toStorage() {
        return {
            id: this.id,
            title: this.title,
            type: this.type
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

}
namespace SteamApp {
    export type Type = string;//"APP" | "DLC";
}

interface SteamAppsExtra extends Storable<Array<StorageType<SteamApp>>> {
    get: Map<SteamApp["id"], SteamApp>["get"];
    has: Map<SteamApp["id"], SteamApp>["has"];
    setType: (id: SteamApp["id"], type: SteamApp.Type) => void;
    push: Array<SteamApp>["push"];
    del: (...id: SteamApp["id"][]) => void;
}
export type SteamApps = Array<SteamApp> & SteamAppsExtra;
export type ReadonlySteamApps = ReadonlyArray<SteamApp> & Pick<SteamAppsExtra, "get" | "has" | "setType">;

export class Profile implements Storable<{
    id: Profile["id"],
    steamApps: StorageType<SteamApps>;
}>{
    readonly id: string;
    readonly text: string;
    steamApps: SteamApps;

    constructor(id: Profile["id"], steamApps: SteamApps = newSteamApps()) {
        id = id.trim();
        this.id = id;
        this.text = id;
        this.steamApps = steamApps;
    }

    toStorage() {
        return {
            id: this.id,
            steamApps: this.steamApps.toStorage()
        };
    }
};

interface ProfilesExtra extends Storable<Array<StorageType<Profile>>> {
    get: Map<Profile["id"], Profile>["get"];
    has: Map<Profile["id"], Profile>["has"];
    push: Array<Profile>["push"];
    del: (...ids: Profile["id"][]) => void;
}
export type Profiles = Array<Profile> & ProfilesExtra;

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
            toStorage() {
                return array.map(steamApp => steamApp.toStorage());
            }
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

export function newProfiles(dbProfiles: StorageType<Profiles>, steamApps: ReadonlySteamApps) {
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
            toStorage() {
                return array.map((profile) => profile.toStorage());
            }
        };

    for (const dbProfile of dbProfiles) {
        const profile = new Profile(
            dbProfile.id,
            dbProfile.steamApps.reduce(
                (acc, dbSteamApp) => {
                    acc.push(new SteamApp(dbSteamApp.id, dbSteamApp.title, dbSteamApp.type));
                    return acc;
                },
                newSteamApps())
        );
        index.set(profile.id, profile);
        array.push(profile);
    }

    return Object.assign(array, extra);
}
