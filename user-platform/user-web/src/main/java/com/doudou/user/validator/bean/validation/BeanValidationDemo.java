package com.doudou.user.validator.bean.validation;

import com.doudou.user.domain.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

public class BeanValidationDemo {

    public static void main(String[] args) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        // cache the factory somewhere
        Validator validator = factory.getValidator();

        User user = new User();
        user.setPassword("***");

        // 校验结果
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        violations.forEach(c -> {
            System.out.println(c.getMessage());
        });
    }
}
