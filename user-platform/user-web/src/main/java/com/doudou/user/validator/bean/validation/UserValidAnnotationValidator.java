package com.doudou.user.validator.bean.validation;

import com.doudou.user.domain.User;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UserValidAnnotationValidator implements ConstraintValidator<UserValid, User> {

    private int idRange;

    public void initialize(UserValid annotation) {
        this.idRange = annotation.idRange();
    }

    @Override
    public boolean isValid(User value, ConstraintValidatorContext context) {

        // 获取模板信息
        context.getDefaultConstraintMessageTemplate();

        return false;
    }
}
