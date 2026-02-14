import { User } from "./user";

export interface Session {
    id: number
    ulid: string
    displayName: string
    email: string
    rol: 'ADMIN' | 'WORKER'
}