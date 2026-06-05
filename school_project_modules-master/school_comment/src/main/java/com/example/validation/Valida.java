package com.example.validation;

import javax.validation.groups.Default;

public interface Valida {
    interface Login extends Default {}
    interface Create extends Default {}
    interface Update extends Default {}
    interface Delete extends Default {}
    interface Query extends Default {}
}
