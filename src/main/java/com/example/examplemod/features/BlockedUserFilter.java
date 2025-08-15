package com.example.examplemod.features;

import lombok.Getter;
import net.minecraft.util.IChatComponent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//TODO: CHANGED HOVER_EVENT TO HOVEREVENT IN 1.8.9
//TODO: ADD AUTOMATIC RELOADER OF NAMES
//TODO: ADD KEYWORD S / "S to also add to keyword
public class BlockedUserFilter {

    public static IChatComponent testing(IChatComponent message, Collection<String> keywords, boolean useHoverEvent) {
//        Text messageLiteral = Text.literal("                >>> CLICK HERE FemboyLunchlies to pick them up! <<<")
//                .setStyle(Style.EMPTY
//                        .withColor(Formatting.AQUA)           // §3 = aqua
//                        .withBold(true)                       // §l = bold
//                        .withClickEvent(new ClickEvent.RunCommand(
//                                "/viewstash material"
//                        ))
//                        .withHoverEvent(new HoverEvent.ShowText(
//                                Text.literal("Click to pickup your materials!")
//                                        .setStyle(Style.EMPTY.withColor(0xFFFF55)) // #FFFF55 = yellow
//                        ))
//                );
//
//        Text message = Text.empty()
//                .append(
//                        Text.literal("                >>> CLICK HERE to pick them up! <<<")
//                                .setStyle(Style.EMPTY
//                                        .withColor(Formatting.AQUA)
//                                        .withBold(true)
//                                )
//                )
//                .append(
//                        Text.literal("                >>> CLICK HERE FemboyLunchlies to pick them up! <<<")
//                                .setStyle(Style.EMPTY
//                                        .withColor(Formatting.AQUA)           // §3 = aqua
//                                        .withBold(true)                       // §l = bold
//                                        .withClickEvent(new ClickEvent.RunCommand(
//                                                "/viewstash material"
//                                        ))
//                                        .withHoverEvent(new HoverEvent.ShowText(
//                                                Text.literal("Click to pickup your materials!")
//                                                        .setStyle(Style.EMPTY.withColor(0xFFFF55)) // #FFFF55 = yellow #0324fc
//                                        ))
//                                )
//                );

        String input = IChatComponent.Serializer.componentToJson(message);


        //!TODO: WHY DID I ADD THIS AGAIN?
        for (String keyword : keywords) {
            Pattern simpleLiteralPattern = Pattern.compile("(?<=[,\\[{])\\s*\"" + keyword + "\"");
            Matcher simpleLiteralMatcher = simpleLiteralPattern.matcher(input);

            String literalReplacement = "{\"text\":\"" + keyword + "\"}";

            input = simpleLiteralMatcher.replaceAll(literalReplacement);
        }
        String output = input;
        int prevOffset = 0;
        //boolean isEmpty = false;
        //
        //if (message.getUnformattedText().contains("empty")) {
        //    isEmpty = true;
        //}
        for (String keyword : keywords) {
            if (!input.contains(keyword)) {
                continue;
            } else {
                int indexOfKeyword = 0;
                List<Integer> positionListOfKeyword = new ArrayList<>();
                while ((indexOfKeyword = input.indexOf(keyword, indexOfKeyword)) != -1) {
                    positionListOfKeyword.add(indexOfKeyword);
                    indexOfKeyword += keyword.length();
                }

                for (Integer posKeyword : positionListOfKeyword) {
                    // Walk backward to find the exact pattern: '":'
                    for (int i = posKeyword; i >= 1; i--) {
                        if (input.charAt(i) == ':' && input.charAt(i - 1) == '"') {
                            // Found '":', extract the key before it
                            int startColumQuote = i - 1;
                            int startQuoteKey = -1;

                            for (int j = startColumQuote - 1; j >= 0; j--) {
                                if (input.charAt(j) == '"') {
                                    startQuoteKey = j;
                                    break;
                                }
                            }

                            if (startQuoteKey != -1) {
                                String key = input.substring(startQuoteKey + 1, startColumQuote);
                                if (key.equals("text")) {
                                    int temp = startQuoteKey - 1;
                                    while (temp >= 1) {
                                        if (input.charAt(temp) == '{' && input.charAt(temp - 1) == ',') {
                                            InitiateReplacement initiateReplacement =
                                                    new InitiateReplacement(
                                                            input, output, keyword, startQuoteKey, prevOffset, useHoverEvent
                                                    );

                                            prevOffset = initiateReplacement.offset;
                                            output = initiateReplacement.getOutput();
                                            break;
                                        } else if (input.charAt(temp) == ':' && input.charAt(temp - 1) == '"') {
                                            int valueKeyEnd = temp - 1;
                                            int valueKeyStart = -1;

                                            for (int j = valueKeyEnd - 1; j >= 0; j--) {
                                                if (input.charAt(j) == '"') {
                                                    valueKeyStart = j;
                                                    break;
                                                }
                                            }

                                            if (valueKeyStart != -1) {
                                                String previousKey = input.substring(valueKeyStart + 1, valueKeyEnd);
                                                if (previousKey.equals("value")) {
                                                    break;
                                                } else {
                                                    InitiateReplacement initiateReplacement =
                                                            new InitiateReplacement(
                                                                    input, output, keyword, startQuoteKey, prevOffset, useHoverEvent
                                                            );

                                                    prevOffset = initiateReplacement.offset;
                                                    output = initiateReplacement.getOutput();
                                                    break;
                                                }
                                            }
                                            break;
                                        }
                                        temp--;
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        //if (!isEmpty) {
        //    output = "{\"text\" : \"\",\"extra\":[".concat(output).concat("]}");
        //}

        IChatComponent replacementMessage = IChatComponent.Serializer.jsonToComponent(output);

        return replacementMessage;
    }

    private static class InitiateReplacement {
        @Getter
        private final String input;
        @Getter
        private String output;
        @Getter
        private final String keyword;
        @Getter
        private final int startQuoteKey;
        @Getter
        private int offset;
        @Getter
        private final boolean useHoverEvent;

        public InitiateReplacement(String input, String output, String keyword, int startQuoteKey, int prevOffset, boolean useHoverEvent) {
            this.input = input;
            this.keyword = keyword;
            this.startQuoteKey = startQuoteKey;
            this.offset = prevOffset;
            this.useHoverEvent = useHoverEvent;

            replace(input, output, keyword, startQuoteKey);
        }

        public void replace(String input, String output, String keyword, int startQuoteKey) {
            ///CHANGE TARGET AND REPLACE
            int startPos = startQuoteKey - 1;  // position of the opening '{'
            JsonExtractResult result = extractJsonObject(input, startPos);
            String jsonObject = result.json;
            startPos = result.startPos;
            //String jsonObject = extractJsonObject(input, startPos);

            assert jsonObject != null;
            int endPos = startPos + jsonObject.length();

            String before = output.substring(0, startPos + offset);
            //TODO: DEBUGGING STATEMENT
            //String target = output.substring(startPos + offset, endPos + offset);
            String after = output.substring(endPos + offset);

            String replacementJson =  doReplacementForText(jsonObject, keyword, useHoverEvent);
            if (!replacementJson.isEmpty()) {
                this.output = before + replacementJson + after;
                this.offset = this.offset + replacementJson.length() - jsonObject.length();
            }
        }
    }

    public static String doReplacementForText(String jsonObject, String keyword, boolean useHoverEvent)
    {
        String outputJson = "";
        //Pattern contentPattern = Pattern.compile("\\{\\s*\"text\"\\s*:\\s*\"(.*?)\"");
        Pattern contentPattern = Pattern.compile("\"text\"\\s*:\\s*\"(.*?)\"\\s*}");
        //ObjectMapper mapper = new ObjectMapper();

        assert jsonObject != null;
        Matcher contentMatcher = contentPattern.matcher(jsonObject);
        //try {
        //    JsonNode root = JsonUtil.MAPPER.readTree(jsonObject);

        if (contentMatcher.find()) {
            //String matchedText = root.path("text").asText();
            String matchedText = contentMatcher.group(1);
            System.out.println(matchedText);

            boolean test = jsonObject.contains(keyword);
            System.out.println(test);

            String beforeKey = "";
            String keywordMatch = "";
            String afterKey = "";

            if (jsonObject.contains(keyword)) {
                int keywordIndex = 0;
                while ((keywordIndex = matchedText.indexOf(keyword, keywordIndex)) != -1) {
                    beforeKey = matchedText.substring(0, keywordIndex);
                    keywordMatch = ModifyKeyStyle(jsonObject, keyword, useHoverEvent);
                    afterKey = matchedText.substring(keywordIndex + keyword.length());

                    if (!beforeKey.isEmpty()) {
                        outputJson = outputJson.concat(jsonObject.replaceAll(Pattern.quote(matchedText), beforeKey).concat(","));
                    }

                    outputJson = outputJson.concat(keywordMatch.replaceAll(Pattern.quote(matchedText), keyword));

                    if (!afterKey.isEmpty()) {
                        outputJson = outputJson.concat(",");
                        outputJson = outputJson.concat(jsonObject.replaceAll(Pattern.quote(matchedText), afterKey));
                    }

                    keywordIndex += keyword.length();
                }
            } else {
                outputJson = jsonObject;
            }

        //} catch (JsonProcessingException e) {
        //    e.printStackTrace();
        }

        return outputJson;
    }

    public static String ModifyKeyStyle(String keywordText, String keyword, boolean useHoverEvent) {
        String customHoverEventColor = "dark_red";
        String customHoverEventValue = "\"text\":\"" + CustomColorUtil.IGNOREHEX + keyword + " has been blocked" + "\",\"color\":\"" + customHoverEventColor + "\"";
        System.out.println("test3");
        // Modify color
        keywordText = keywordText.replaceAll("\"color\"\\s*:\\s*\"[^\"]*\"", "\"color\":\"dark_red\"");
        //TODO: NOT REALLY SAVE
        keywordText = keywordText.replaceAll("\"text\"\\s*:\\s*\"[^\"]*\"", "\"text\":\"" + keyword + "\"");
        //!TODO: ONLY LOOK AT ONE LAYER
        if (!keywordText.contains("\"color\"")) {
            keywordText = keywordText.replaceFirst("(\"text\"\\s*:\\s*\"[^\"]*\")", "$1,\"color\":\"dark_red\"");
        }

        //TODO: DONT REPLACE EVYTHRING JUST TEXT AND COLOR
        // Modify or insert hoverEvent
        if  (useHoverEvent) {
            if (keywordText.contains("\"hoverEvent\"")) {
                int hoverEventIndex = keywordText.indexOf("hoverEvent");
                JsonExtractResult result = extractJsonObject(keywordText, hoverEventIndex);
                String afterHoverEvent = result.json;

                String replacementHoverEvent = afterHoverEvent.replaceAll("\"text\"\\s*:\\s*\"[^\"]*\"", "\"text\":\"" + keyword + " has been blocked\"");
                replacementHoverEvent = replacementHoverEvent.replaceAll("\"color\"\\s*:\\s*\"[^\"]*\"", "\"color\":\"" + customHoverEventColor + "\"");

                keywordText = keywordText.replaceAll(Pattern.quote(afterHoverEvent), replacementHoverEvent);

//                keywordText = keywordText.replaceAll(
//                        Pattern.quote(afterHoverEvent),
//                        "hoverEvent\":{\"value\":{" + customHoverEventValue + "},\"action\":\"show_text\"}"
//                );
            } else {
                keywordText = keywordText.replaceFirst(
                        "(\"text\"\\s*:\\s*\"[^\"]*\")",
                        "$1,\"hoverEvent\":{\"value\":{" + customHoverEventValue + "},\"action\":\"show_text\"}"
                );
            }
        }
        System.out.println("test4");
        return keywordText;

    }

    public static JsonExtractResult extractJsonObject(String text, int startPos) {
        int braceCount = 0;
        boolean started = false;

        // Find actual start '{' going backward
        for (int i = startPos; i >= 0; i--) {
            char c = text.charAt(i);
            if (c == '{') {
                startPos = i;
                break;
            }
        }

        for (int i = startPos; i < text.length(); i++) {
            char c = text.charAt(i);

            if (c == '{') {
                braceCount++;
                started = true;
            } else if (c == '}') {
                braceCount--;
            }

            if (started && braceCount == 0) {
                String json = text.substring(startPos, i + 1);
                return new JsonExtractResult(json, startPos);
            }
        }
        return null;
    }

    public static class JsonExtractResult {
        public final String json;
        public final int startPos;

        public JsonExtractResult(String json, int startPos) {
            this.json = json;
            this.startPos = startPos;
        }
    }
    //public static String extractJsonObject(String text, int startPos) {
    //    int braceCount = 0;
    //    boolean started = false;
//
    //    for (int i = startPos; i >= 0; i--) {
    //        char c = text.charAt(i);
//
    //        if (c == '{') {
    //            startPos = i;
    //            break;
    //        }
    //    }
//
    //    for (int i = startPos; i < text.length(); i++) {
    //        char c = text.charAt(i);
//
    //        if (c == '{') {
    //            braceCount++;
    //            started = true;
    //        } else if (c == '}') {
    //            braceCount--;
    //        }
//
    //        if (started && braceCount == 0) {
    //            return text.substring(startPos, i + 1);
    //        }
    //    }
    //    return null;
    //}
}