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