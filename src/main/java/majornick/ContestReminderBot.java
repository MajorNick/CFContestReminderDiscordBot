package majornick;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public class ContestReminderBot {
    public ContestReminderBot() {
    }

    public void start() {
        DiscordClient client = DiscordClient.create("{Token}");
        Mono<Void> login = client.withGateway((GatewayDiscordClient gateway) -> gateway.on(MessageCreateEvent.class, event -> {
            Message message = event.getMessage();


            if (message.getContent().equals("!dc")) {
                return message.getChannel().flatMap(ch -> ch.createMessage("baro"));
            }
            return Mono.empty();
        }));
        login.block();
    }
}
