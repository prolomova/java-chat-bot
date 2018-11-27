import static org.junit.jupiter.api.Assertions.*;

import org.glassfish.grizzly.utils.Pair;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.ArrayList;

class Tests
{
    @Test
    void testIncorrectAnswerWinx() throws FileNotFoundException {
        var gameInstance = new WinxQuiz("winx");
        gameInstance.markActive(0);
        var answer = "???";
        gameInstance.proceedRequest("", 0);
        ChatBotReply curReply = gameInstance.proceedRequest(answer + "", 0);
        assertEquals(curReply.message, "Подумай ещё раз!");
    }

    @Test
    void testIncorrectAnswerPixie() throws FileNotFoundException {
        var gameInstance = new PixieQuiz("pixie.yml");
        gameInstance.markActive(0);
        var answer = "???";
        gameInstance.proceedRequest("", 0);
        ChatBotReply curReply = gameInstance.proceedRequest(answer + "", 0);
        assertEquals(curReply.message, "Подумай ещё раз!");
    }


    @Test
    void testCorrectCharactersWinx() throws FileNotFoundException {
        var answers = new String[] {
                "Сильная и уверенная.",
                "Общительная и весёлая.",
                "Мечтательная и ранимая.",
                "Рассудительная и рациональная.",
                "Спокойная и умиротворяющая.",
                "Эмоциональная и непосредственная.",

                "Буду сильно переживать и нервничать.",
                "Не страшно, спишу у подруги.",
                "Подумаешь контрольная, даже если и провалю - это не главное в жизни.",
                "Пусть я и не готовилась специально, я достаточно хорошо знаю предмет, чтобы получить нормальную оценку.",
                "Что значит 'не готова'??",
                "Сперва надо хорошенько выспаться, а завтра разберёмся. Сейчас уже всё равно ничего не исправишь.",

                "Интуиция",
                "Чувство юмора",
                "Невозмутимость",
                "Железная логика",
                "Искренность",
                "Оптимизм",

                "Требовательность к себе",
                "Легкомысленность",
                "Ранимость",
                "Импульсивноть",
                "Застенчивость",
                "Неумение выражать эмоции",

                "Бирюзовый.",
                "Жёлтый.",
                "Красный.",
                "Фиолетовый.",
                "Розовый.",
                "Зелёный.",

                "С прикольными опытами - физика, химия",
                "Терпеть не могу школу - скукота!",
                "Художественные науки - музыка, рисование",
                "Точные науки - математика, программирование",
                "Естественные науки - биология, ботаника",
                "Физкульутра",

                "Встретиться с друзьями.",
                "Отправиться по магазинам.",
                "Послушать музыку в своей комнате, чтобы никто не мешал.",
                "Поиграть в компьютерные игры.",
                "Принять ванну с пеной.",
                "Сходить в спорт-клуб или бассейн.",

                "Постараюсь почаще быть рядом и помогать ему.",
                "Пофлиртую с ним.",
                "Буду его подкалывать и спорить с ним при каждом удобном случае.",
                "Добавлю его в друзья в социальной сети.",
                "Буду тайно надеяться на то, что он подойдёт ко мне первым.",
                "Дождусь, когда мы будем наедине, и прямо скажу, что он мне нравится.",

                "Путешественницей, исследующей неизведанные уголки Земли или даже других планет!",
                "Известной актрисой или моделью",
                "Музыкантом или певицей",
                "Учёным или программистом",
                "Врачом или учителем",
                "Спортсменкой или балериной"};

        var characters = new String[] {"Блум", "Стелла", "Муза", "Текна", "Флора", "Лейла"};
        var gameInstance = new WinxQuiz("winx");
        gameInstance.markActive(0);
        ChatBotReply curReply = gameInstance.proceedRequest("", 0);
        for(var j = 0; j < characters.length; j++){
            for (var i = j; i < answers.length; i += characters.length){
                var index = 'A';
                for (var k = 0; k < curReply.keyboardOptions.size(); k++) {
                    if (curReply.keyboardOptions.get(k).endsWith(answers[i])) {
                        index += k;
                        break;
                    }
                }
                curReply = gameInstance.proceedRequest(index + "", j);

            }
            assertEquals("Всё понятно. Ты " + characters[j], curReply.message);
            gameInstance.markActive(j + 1);
            curReply = gameInstance.proceedRequest("", j + 1);
        }
    }

    @Test
    void testCorrectCharacterPixie() throws FileNotFoundException {
        var gameInstance = new PixieQuiz("pixie.yml");
        gameInstance.markActive(0);
        ChatBotReply curReply = gameInstance.proceedRequest("", 0);
        while (curReply.keyboardOptions != null) {
            curReply = gameInstance.proceedRequest(curReply.keyboardOptions.get(0), 0);
        }
        assertTrue(curReply.message.startsWith("Всё понятно. Твоя winx пикси - Диджит"));
    }

    @Test
    void testAnswerWhenGameIsEnded() {
        var tests = new ArrayList<Pair<String, Class<? extends IGame>>>();
        tests.add(new Pair<>("winx", WinxQuiz.class));
        tests.add(new Pair<>("pixie.yml", PixieQuiz.class));
        var chatBot = new ChatBot(new GameFactory(), tests);
        chatBot.answer("Старт", 0);
        chatBot.answer("Стоп", 0);
        var reply = chatBot.answer("???", 0);
        assertEquals("Игра ещё не началась.", reply.message);
    }

    @Test
    void testAnswerWhenGameStarts() {
        var tests = new ArrayList<Pair<String, Class<? extends IGame>>>();
        tests.add(new Pair<>("winx", WinxQuiz.class));
        tests.add(new Pair<>("pixie.yml", PixieQuiz.class));
        var chatBot = new ChatBot(new GameFactory(), tests);
        var reply = chatBot.answer("Старт", 0);
        assertEquals("Привет! Сейчас мы узнаем, кто ты из фей Winx.\n" +
                "По характеру ты...", reply.message);
    }

    @Test
    void testAnswerWhenGameIsStopped() {
        var tests = new ArrayList<Pair<String, Class<? extends IGame>>>();
        tests.add(new Pair<>("winx", WinxQuiz.class));
        tests.add(new Pair<>("pixie.yml", PixieQuiz.class));
        var chatBot = new ChatBot(new GameFactory(), tests);
        chatBot.answer("Старт", 0);
        var reply = chatBot.answer("Стоп", 0);
        assertEquals("Игра закончена.", reply.message);
    }
}
