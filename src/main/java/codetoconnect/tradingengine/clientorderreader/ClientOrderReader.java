package codetoconnect.tradingengine.clientorderreader;

import codetoconnect.tradingengine.clientorderreader.exceptions.InvalidFixTagException;
import codetoconnect.tradingengine.clientorderreader.exceptions.MissingFixTagException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientOrderReader {

    private static final String SIDE_TAG = "54";
    private static final String ORDER_TYPE_TAG = "40";
    private static final String ORDER_QUANTITY_TAG = "38";
    private static final String TARGET_PERCENTAGE_TAG = "6404";

    public static ClientPovBuyOrder readFromInput(String clientOrder) throws InvalidFixTagException, MissingFixTagException {
        Map<String, String> fixTags = parseInput(clientOrder);

        verifyRequiredTagsArePresent(fixTags);

        Integer orderQuantity = Integer.parseInt(fixTags.get(ORDER_QUANTITY_TAG));
        Double targetPercentage = Double.parseDouble(fixTags.get(TARGET_PERCENTAGE_TAG));

        return new ClientPovBuyOrder(
                orderQuantity,
                targetPercentage
        );
    }

    private static Map<String, String> parseInput(String clientOrder) throws InvalidFixTagException {
        Map<String, String> fixTags = new HashMap<>();

        String[] tags = clientOrder.split(";");

        for (String tag : tags) {
            String[] tagAndValue = tag.trim().split("=");

            if (tagAndValue.length != 2) {
                throw new InvalidFixTagException(String.join("=", tagAndValue));
            }

            String tagKey = tagAndValue[0];
            String value = tagAndValue[1];

            fixTags.put(tagKey, value);
        }

        return fixTags;
    }

    private static void verifyRequiredTagsArePresent(Map<String, String> fixTags) throws MissingFixTagException {
        List<String> requiredTags = List.of(SIDE_TAG, ORDER_TYPE_TAG, ORDER_QUANTITY_TAG, TARGET_PERCENTAGE_TAG);

        for (String requiredTag : requiredTags) {
            if (!fixTags.containsKey(requiredTag)) {
                throw new MissingFixTagException(requiredTag);
            }
        }

    }
}

