package com.abo2.recode.dto.user;

import com.abo2.recode.domain.user.User;
import com.abo2.recode.domain.user.UserEnum;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class UserReqDto {

    @Setter
    @Getter
    public static class LoginReqDto {
        private String username;
        private String password;
    }

    @Getter
    @Setter
    public static class JoinReqDto {
        @Pattern(regexp = "^[a-zA-Z가-힣0-9]{1,20}$", message = "영문/숫자 2~20자 이내로 작성해주세요")
        @NotEmpty
        private String username;

        @NotEmpty
        @Size(min = 4, max = 20)
        private String password;

        @NotEmpty
        @Pattern(regexp = "^[a-zA-Z0-9]{2,20}@[a-zA-Z0-9]{2,6}\\.[a-zA-Z]{2,3}$", message = "이메일 형식으로 작성해주세요")
        private String email;

        @NotEmpty
        @Pattern(regexp = "^[a-zA-Z가-힣0-9]{1,20}$", message = "한글/영문 1~20자 이내로 작성해주세요")
        private String nickname;

        public User toEntity(BCryptPasswordEncoder passwordEncoder) {
            return User.builder()
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .email(email)
                    .nickname(nickname)
                    .role(UserEnum.CUSTOMER)
                    .build();
        }
    }

    @Getter
    @Setter
    public static class JoinAdminReqDto {
        @Pattern(regexp = "^[a-zA-Z0-9]{2,20}$", message = "영문/숫자 2~20자 이내로 작성해주세요")
        @NotEmpty
        private String username;

        @NotEmpty
        @Size(min = 4, max = 20)
        private String password;


        @NotEmpty
        @Pattern(regexp = "^[a-zA-Z0-9]{2,10}@[a-zA-Z0-9]{2,6}\\.[a-zA-Z]{2,3}$", message = "이메일 형식으로 작성해주세요")
        private String email;

        @NotEmpty
        @Pattern(regexp = "^[a-zA-Z가-힣0-9]{1,20}$", message = "한글/영문 1~20자 이내로 작성해주세요")
        private String nickname;

        public User toEntity(BCryptPasswordEncoder passwordEncoder) {
            return User.builder()
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .email(email)
                    .nickname(nickname)
                    .role(UserEnum.ADMIN)
                    .build();
        }
    }

    @Setter
    @Getter
    public static class FindUsernameReqDto{
        @NotEmpty
        @Pattern(regexp = "^[a-zA-Z0-9]{2,10}@[a-zA-Z0-9]{2,6}\\.[a-zA-Z]{2,3}$", message = "이메일 형식으로 작성해주세요")
        private String email;
    }


    @Setter
    @Getter
    public static class UpdateUserReqDto{
        @NotEmpty
        @Pattern(regexp = "^[a-zA-Z0-9]{2,20}@[a-zA-Z0-9]{2,6}\\.[a-zA-Z]{2,3}$", message = "이메일 형식으로 작성해주세요")
        private String email;

        @NotEmpty
        @Pattern(regexp = "^[a-zA-Z가-힣]{1,20}$", message = "한글/영문 1~20자 이내로 작성해주세요")
        private String nickname;
    }

    @Setter
    @Getter
    public static class WriteEssayReqDto{
        @NotEmpty
        @Size(min = 2,  max = 300)
        private String essay;
    }

    @Setter
    @Getter
    public static class ChangePasswordReqDto{
        @NotEmpty
        private String password;
        private String emailCheckToken;
    }

}
