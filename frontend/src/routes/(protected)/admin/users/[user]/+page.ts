interface UserPageParams {
  user: string;
}

export const load = async ({ params }: { params: UserPageParams }) => ({ user: params.user });