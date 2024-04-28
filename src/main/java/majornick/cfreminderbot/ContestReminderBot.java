package majornick.cfreminderbot;

import com.fasterxml.jackson.databind.ObjectMapper;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import majornick.cfreminderbot.models.CFResponse;
import majornick.cfreminderbot.models.Contest;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ContestReminderBot {
    private final String CF_CONTEST_LIST = "https://codeforces.com/api/contest.list?gym=false";
    private final String CHANNEL_ID = "{ChanelId}";

    public void start() {
        DiscordClient client = DiscordClient.create("{TokenId}");
        scheduleMessage(client);
        scheduleCFContestParsing(client);
        Mono<Void> login = client.withGateway((GatewayDiscordClient gateway) -> gateway.on(MessageCreateEvent.class, event -> {
            Message message = event.getMessage();
            String content = message.getContent();
            if (content.startsWith("!ct ") || content.equals("!ct")) {
                String[] args = content.split("\\s+");
                String answer;
                if(args.length==2 &&args[1].equals("list")){
                    var contestOpt = getTodaysContest();
                    if(contestOpt.isPresent()){
                        var contest = contestOpt.get();
                        answer  = String.format("Contest: %s At: %s",contest.getName(),contest.getStartTime());
                    }else{
                        answer = "There is no contest today.";
                    }

                }else{
                    answer = "Wrong command.";
                }
                return message.getChannel().flatMap(ch -> ch.createMessage(answer));
            }
            return Mono.empty();
        }));
        login.block();
    }

    private void scheduleCFContestParsing(DiscordClient client) {
        long initialDelay = Duration.between(LocalTime.now(), LocalTime.of(0, 1))
                .toSeconds();
        if (initialDelay < 0) {
            initialDelay += Duration.ofDays(1).toSeconds();
        }
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
        scheduler.scheduleAtFixedRate(() -> {
            scheduleMessage(client);
        }, initialDelay, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);
    }

    private void scheduleMessage(DiscordClient client) {
        Optional<Contest> contestOPT = getTodaysContest();
        if (contestOPT.isPresent()) {
            Contest c = contestOPT.get();
            long delayForToday = Duration.between(LocalDateTime.now(), c.getStartTime().minusMinutes(30)).toMillis();
            ScheduledExecutorService messageScheduler = Executors.newScheduledThreadPool(1);
            messageScheduler.schedule(() -> {
                client.getChannelById(Snowflake.of(CHANNEL_ID)).createMessage(createReminderMessage(c)).subscribe();
            }, delayForToday, TimeUnit.MILLISECONDS);
        }
    }


    private Optional<Contest> getTodaysContest() {
        List<Contest> contests = getActiveContests();
        Contest todaysContest = null;
        for (Contest c : contests) {

            if (c.getPhase().equalsIgnoreCase("BEFORE")) {
                Instant instant = Instant.ofEpochSecond(Long.parseLong(c.getStartTimeSeconds()));
                LocalDateTime contestDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
                if (LocalDateTime.now().getDayOfYear() == contestDateTime.getDayOfYear()) {
                    todaysContest = c;
                    c.setStartTime(contestDateTime);
                    break;
                }
            }
        }
        return Optional.ofNullable(todaysContest);
    }

    private List<Contest> getActiveContests() {
        HttpClient httpClient = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(CF_CONTEST_LIST))
                .GET()
                .build();
        try {
            String respBody = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(respBody, CFResponse.class).getResult();
        } catch (InterruptedException | IOException e) {
            System.err.println(e.getMessage());
        }
        return Collections.emptyList();
    }

    private String createReminderMessage(Contest c) {
        return String.format("@everyone CONTEST REMINDER!\n %s in %d minutes\n", c.getName(),Duration.between(LocalDateTime.now(),c.getStartTime()).toMinutes());
    }
}
