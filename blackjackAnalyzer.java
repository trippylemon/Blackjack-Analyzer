import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class blackjackAnalyzer {
    //change the input/output files if required
    private static final String INPUT_FILE = "input/game_data_2.txt";
    private static final String OUTPUT_FILE = "output/analyzer_output_2.txt";
    private static final String DELIMITER = ",";
    static String[] suits = {"S", "D", "H", "C"};
    static String[] cards = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "j", "J", "q", "Q", "k", "K", "a", "A"};

    public static void main(String[] args) throws Exception
    {   
    	//get faulty moves
        ArrayList<String> lines = readData();
        
        //sorting
        lines = sortData(lines);
        
        //analyzeData
        lines = validateData(lines);
        //writing to a file
        writeData(lines);
    }


/* 
 * readData is a function that reads the file and stores every line in an arraylist called lines
 * after that data is passed to be sorted and then to be analyzed
*/
    private static ArrayList<String> readData()
    {
            ArrayList<String> lines = new ArrayList<String>();
            try (Scanner scanner = new Scanner(new File(INPUT_FILE)))
            {
                while (scanner.hasNextLine())
                {
                    String line = scanner.nextLine();
                    lines.add(line);
                }
            }
        catch (IOException e)
            {
                System.out.println("Error reading file " + INPUT_FILE);
            }
            return lines;
    }

/*  
 *  This is a function that takes the arraylist and sorts it by second element and then by first element of the line 
 *  which is the sessionID and the timestamp in the game. 
 *  The function returns the sorted arraylist
*/
    private static ArrayList<String> sortData(ArrayList<String> lines)
    {
        List<String[]> splitLines = new ArrayList<>();
        for (String line : lines)
        {
            String[] splitLine = line.split(",");
            if (splitLine.length != 6)
            {
                //wrong input for line
                continue;
            }
            splitLines.add(splitLine);
        }

        Collections.sort(splitLines, Comparator.comparingInt(line -> Integer.parseInt(line[1])));

        List<String> sortedLines = new ArrayList<>();
        for (String[] splitLine : splitLines)
        {
            sortedLines.add(String.join(",", splitLine));
        }
        return (ArrayList<String>) sortedLines;
    }
/* 
 * This function splits data into parts like action, dealer's and player's cards
 * and then checks if the action is valid or not
 * if it is not valid it is added to the arraylist called invalidLines
 * and then the function returns the arraylist
 */
    private static ArrayList<String> validateData(ArrayList<String> lines) 
    {
        int previousSessionID = -1;
        
        ArrayList<String> invalidLines = new ArrayList<>();
        for (String line : lines) {
            String[] parts = line.split(DELIMITER);
            int sessionId = Integer.parseInt(parts[1]);
            String action = parts[3];
            String[] dealerHandCards = parts[4].split("-");
            String[] playerHandCards = parts[5].split("-");
                        
            if (!isValidTurn(action, playerHandCards, dealerHandCards)) 
            {
                if (sessionId != previousSessionID)
                {
                    invalidLines.add(line);
                    System.out.println("Error: Invalid turn in line " + line);
                    previousSessionID = sessionId;
                    continue;
                }
            }
        }
        return invalidLines;
    }
/*
 * This function calculates the total of the cards in the hand
 * and returns the total
 */
    private static int getTotal(String[] cards) 
    {
        int sum = 0;
        String[] higherCards = {"K", "k", "q", "Q", "j", "J"};
        for (int i = 0; i < cards.length; i++) 
        {
            String firstChar = String.valueOf(cards[i].charAt(0));
            if (Arrays.asList(higherCards).contains(firstChar))
            {
                sum += 10;	
            } else if ((cards[i].charAt(0) == ('A')) || (cards[i].charAt(0) == ('a')))
            {
                sum += 11;
            } else {
                if (Character.isDigit(cards[i].charAt(0)))
                {
                    sum = sum + (int) cards[i].charAt(0) - '0';
                }
            }
        }
        return sum;
    }

/*
 * This function checks if the turn is valid or not
 * and returns true or false
 * valid turns are based on the logic of the game, not how it is written
 */
    private static boolean isValidTurn(String action, String[] playerHandCards, String[] dealerHandCards) {
        int playerTotal = getTotal(playerHandCards);
        int dealerTotal = getTotal(dealerHandCards);
    
        // Check for dealer's face-down cards
        if (dealerHandCards.length >= playerHandCards.length + 1) {
            for (int i = 0; i < playerHandCards.length; i++) {
                if (dealerHandCards[i].equals("?") && dealerHandCards[i+1].equals("?")) {
                    return false;
                }
            }
        }
    
        // Check for other conditions
        if (dealerTotal >= 17 && playerTotal >= 17 && action.contains("Hit"))
        {
            return false;
        }
        if ((dealerTotal > 21 && playerTotal > 21 && !action.contains("Redeal"))
                || (dealerTotal >= 21 || playerTotal >= 21) && action.contains("Hit")
                || (dealerTotal > 21 && playerTotal <= 21 && action.contains("P Lose"))
                || (playerTotal > 21 && dealerTotal <= 21 && (action.contains("D Lose") || action.contains("P Stand")))
                || (playerTotal > 17 && action.contains("P Joined") && dealerTotal == 0)
                || (playerTotal == dealerTotal && action.contains("Win"))
                || (playerTotal > dealerTotal && playerTotal <= 21 && action.contains("P Lose"))
                || (playerTotal < dealerTotal && dealerTotal <= 21 && action.contains("P Win"))
                || (playerTotal > 19 && action.contains("P Hit"))
                || (playerTotal == dealerTotal && action.contains("P Joined"))) 
        {
            return false;
        }
        return true;
    }
/*
 * This function writes the data to the output file
 */
    private static void writeData(List<String> myList)
    {
        try
        {
            FileWriter writer = new FileWriter(OUTPUT_FILE);
            for (String str : myList)
            {
                writer.write(str + System.lineSeparator());
            }
            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}

