package helpers;

/**
 * This class is to capitalize location name, if they aren't already.
 */
public class LocationNameConverter {

    /**
     * The method to capitalize name.
     * @param input is the location that the user enters.
     * @return A String that is the capitalized version of user's input.
     */
    public static String capitalizeLocationName(String input) {
        final String[] words = input.toLowerCase().split(" ");
        final StringBuilder properSpelling = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                properSpelling.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append(" ");
            }
        }
        return properSpelling.toString().trim();
    }
}
