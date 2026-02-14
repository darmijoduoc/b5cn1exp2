export interface RoutePoint {
    id?: number;
    name: string;
    position: number;
}

export interface Route {
    id?: number;
    ulid: string;
    code: string;
    title: string;
    description: string;
    points: RoutePoint[];
    previousRoute?: Route;
}

export interface Vehicle {
    id?: number;
    ulid: string;
    plate: string;
    type: string;
    status: string;
    currentAddress: string;
    route?: Route;
}