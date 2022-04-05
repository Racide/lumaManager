
export interface JSONSteamApp {
    [key: number]: {
        data: {
            type: string;
        };
    };
}

export interface SteamApp {
    readonly id: number,
    readonly title: string,
    type?: string;
};

namespace SteamApp {
    export type Type = "APP" | "DLC";
}

export interface SteamData extends ReadonlyArray<SteamApp> {
    get: (id: SteamApp["id"]) => void;
    has: (id: SteamApp["id"]) => void;
    setType: (id: SteamApp["id"], type: SteamApp.Type) => void;
}

export class Profile {
    readonly id: string;
    readonly text: string;
    // readonly name: string;
    steamApps: SteamApp[];

    constructor(id: Profile["id"], steamApps: SteamApp[] = []) {
        id = id.trim();
        this.id = id; //newId();
        // this.name = name;
        this.text = id;
        this.steamApps = steamApps;
    }
};

export class Profiles extends Array<Profile>{
    private index: Map<Profile["id"], Profile> = new Map();

    get(id: Profile["id"],) {
        return this.index.get(id);
    }
    has(id: Profile["id"],) {
        return this.index.has(id);
    }
    push(...profiles: Profile[]) {
        profiles.forEach(e => this.index.set(e.id, e));
        return super.push(...profiles);
    }
    del(id: Profile["id"],) {
        super.splice(super.indexOf(this.index.get(id)!), 1);
        this.index.delete(id);
    }
};
