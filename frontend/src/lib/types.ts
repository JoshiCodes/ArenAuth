type Me = {
    username: string;
    userId: string;
    roles: string[];
    avatarId: string | null;
};

type FullUser = {
    id: string,
    name: string,
    email: string | null,
    avatarId: string | null,
    roles: string[],
    projects: string[] | null
}

type FullProject = {
    id: string,
    name: string,
    description: string,
    avatarId: string | null,
    ownerId: string,
    redirectUris: string[],
    createdAt: Date
}