package com.doudou.user.service;

import com.doudou.user.domain.User;
import com.doudou.user.repository.UserRepository;
import com.doudou.user.utils.Md5Utils;

import javax.annotation.Resource;
import javax.validation.Validator;

/**
 * 用户服务
 */
public class UserServiceImpl implements UserService {

    @Resource(name = "bean/UserRepository")
    private UserRepository userRepository;

    @Resource(name = "bean/Validator")
    private Validator validator;

    @Override
    public boolean register(User user) {
        String password = Md5Utils.encode(user.getPassword());
        user.setPassword(password);
        return userRepository.save(user);
    }

    @Override
    public boolean deregister(User user) {
        return userRepository.deleteById(user.getId());
    }

    @Override
    public boolean update(User user) {
        return userRepository.update(user);
    }

    @Override
    public User queryUserById(Long id) {
        return userRepository.getById(id);
    }

    @Override
    public User queryUserByNameAndPassword(String name, String password) {
        return userRepository.getByNameAndPassword(name, password);
    }
}
