package com.ecom.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserNotVerifiedException extends Exception{

    private boolean newEmailSent;

}
