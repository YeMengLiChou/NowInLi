package cn.li.nowinli;

import cn.li.nowinli.User;

interface IUserManager {
    List<User> getUsers();

    void addUser(in User user);
}