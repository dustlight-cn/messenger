package plus.messenger.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import plus.messenger.core.services.ChannelService;
import plus.messenger.core.services.UserService;

@Configuration
public class EmailNotifierConfiguration {

    @Bean
    public EmailNotifier emailNotifier(@Autowired UserService userService,
                                                  @Autowired ChannelService channelService,
                                                  @Autowired JavaMailSender javaMailSender,
                                                  @Autowired MailProperties mailProperties) {
        return new EmailNotifier(channelService,userService,javaMailSender,mailProperties);
    }
}
