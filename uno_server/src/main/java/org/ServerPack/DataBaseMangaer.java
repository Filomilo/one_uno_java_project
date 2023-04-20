package org.ServerPack;

import org.SharedPack.UnoCard;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * a class that has method for managing and geting data from oracle data base
 */
public class DataBaseMangaer {


    /**
     * a varaible to store data base connection
     */
    private Connection connection;
    /**
     * a varaivle to store data base port
     */
    private String dataBasePort;
    /**
     * a varaible to store data base adress
     */
    private String dataBaseAdres;
    /**
     * a varaible to store data base name
     */
    private String dataBaseName;
    /**
     * variable to store data base username
     */
    private String dataBaseUserName;
    /**
     * varaible to store data base password
     */
    private String dataBasePass;

    /**
     * setter for data base name
     * @param dataBaseName
     */
    public void setDataBaseName(String dataBaseName) {
        this.dataBaseName = dataBaseName;
    }

    /**
     * setter for data abse port
     * @param dataBasePort
     */
    public void setDataBasePort(String dataBasePort) {
        this.dataBasePort = dataBasePort;
    }

    /**
     * settter for data base username
     * @param dataBaseUserName
     */
    public void setDataBaseUserName(String dataBaseUserName) {
        this.dataBaseUserName = dataBaseUserName;
    }

    /**
     * setter for data base password
     * @param dataBasePass
     */
    public void setDataBasePass(String dataBasePass) {
        this.dataBasePass = dataBasePass;
    }

    /**
     * setter for data base adress
     * @param dataBaseAdres
     */
    public void setDataBaseAdres(String dataBaseAdres) {
        this.dataBaseAdres = dataBaseAdres;
    }

    /**
     * object used for synchorozation method
     */
    private final Object semaphore=new Object();

    /**
     * a basci constuctor for data base
     */
    DataBaseMangaer()
    {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * a method taht setups connection with data base
     * @return
     */
    public boolean connectWithDataBase()
    {
        try {
            this.connection= DriverManager.getConnection("jdbc:oracle:thin:@"+this.dataBaseAdres + ":"+ this.dataBasePort +"/"+this.dataBaseName , this.dataBaseUserName, this.dataBasePass);
          //  this.connection= DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:"+ +" as SYSDBA");
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }


    /**
     * Function removes all tables procedures etc. And then creates all from the start to make sure all things in data base are prepared for game
     */
    public void resetDataBase()
    {

      this.dropSeq();
      System.out.println("droped seq");
     this.dropTables(); this.createSeq();
      this.createTables();
        this.createProcedures();
        this.createFunctions();
      this.createViews();
       this.createBaseCards();


        System.out.println("fisned creat" );
    }


    /**
     * a method that checks if tables aree corecct in data base
     * @return
     * @throws SQLException
     */
    public boolean checkTable() throws SQLException {
        DatabaseMetaData metadata= this.connection.getMetaData();
        String[] types={"TABLE"};
        String[] searchedTablesArray={"PLAYERS","GAMES","CARDS","ACTIVE_CARD_PLACES"};
        List<String> searchedTables= new ArrayList<String>();
        searchedTables.addAll(Arrays.asList(searchedTablesArray));
        ResultSet resultSet=metadata.getTables(null,null,"%", types);
        while(resultSet.next()) {
        searchedTables.remove(resultSet.getString("TABLE_NAME"));
        }
        return searchedTables.size()==0;
        }

    /**
     * a method taht created all neccesary tables in data base
     */
    private void createTables()
    {
        executeArrayStatements(SqlScripts.CreateTablecSripts);
    }

    /**
     * a method taht drops all uno game tables from data base
     */
    private void dropTables()
    {
        executeArrayStatements(SqlScripts.DropTablecSripts);
    }



    /**
     * a method taht creates views in database
     */
    private void createViews()
    {
        executeArrayStatements(SqlScripts.CreateViewsScripts);
    }

    /**
     * a method that creates swquance in database
     */
    private void createSeq()
    {
        executeArrayStatements(SqlScripts.CreateSequencesScripts);
    }

    /**
     * a method that drop sequances in data base
     */
    private void dropSeq()
    {
        executeArrayStatements(SqlScripts.DropSequencesScripts);
    }

    /**
     * a method that create fucntions in data base
     */
    private void createFunctions()
    {
        executeArrayStatements(SqlScripts.CreateFunctions);
    }

    /**
     * a method that created procedures in data base
     */
    private void createProcedures()
    {
        executeArrayStatements(SqlScripts.CreateProceduresScripts);
    }

    /**
     * a method that add to data base provided player
     * @param nick
     * @param pass
     * @return
     */
    public boolean addPlayer(String nick, String pass)
    {
        String[] arr={nick,pass};
        boolean res= executeProcedure(SqlScripts.AddPlayerScript,arr);
        return res;
    }

    /**
     * a method that cals data abse to fill it with base cards
     */
    private void createBaseCards()
    {
        executeProcedure(SqlScripts.FillBaseCardsScripts);
    }

    /**
     * a method taht cals data base to crete new game in data base
     * @param nick
     */
    public void createNewGame(String nick)
    {
        String[] arr={nick};
        executeProcedure(SqlScripts.CreateNewGameScripts,arr);
    }

    /**
     * a method that calexecute data base to add provided player to game
     * @param nick
     */
    public void addPlayerToGame(String nick)
    {
        String[] arr={nick};
        executeProcedure(SqlScripts.AddPlayerToGameScript,arr);
    }

    /**
     * a method that cals data base to draw card for specfic player
     * @param nick
     */
    public void drawCard(String nick)
    {
        String[] arr={nick};
        executeProcedure(SqlScripts.DrawCardScript,arr);
    }

    /**
     * a method taht calls data base to prepare deck for game
     */
    public void preapreDeck()
    {
        executeProcedure(SqlScripts.PreapareDeckScript);
    }

    /**
     * a method that calls database to deal cards to players
     */
    public void dealCards()
    {
        executeProcedure(SqlScripts.DealCardScript);
    }

    /**
     * a method taht calls data base to play card from specific player on specific position in hand
     * @param nick
     * @param pos
     */
    public void playCard(String nick, int pos)
    {
        executeProcedure(SqlScripts.PlayCardScript,nick,pos);
    }

    /**
     * a method taht calls data base to reshufle deck
     */
    public void reshuffleDeck()
    {
        executeProcedure(SqlScripts.ReshuffleDeck);
    }

    /**
     * a method that surrenders player in data base
     * @param nick
     */
    public    void surrender(String nick)
    {
        String[] arr={nick};
        executeProcedure(SqlScripts.SurrenderScript,arr);
    }

    /**
     * a method taht sets rank positoin in data base for sepcific player
     * @param nick
     */
    public    void setRank(String nick)
    {
        String[] arr={nick};
        executeProcedure(SqlScripts.SetRankScript,arr);
    }


    /**
     * a mehtod that returns amount of cards on stack
     * @return
     */
    public  int getNmOnStack()
    {
        return executeFunciton(SqlScripts.GetCardAmtOnStack);
    }

    /**
     * a method that returns amount card hold by chosen player
     * @param nick
     * @return
     */
    public  int getAmtInHand(String nick)
    {
        String[] arr={nick};
        return executeSelectInt(SqlScripts.GetAMtOfCardInHands,arr );
    }


    /**
     * a method taht return amount of active players from data base
     * @return
     */
    public    int getAmtActivePlayers()
    {
        String[] arr=new String[0];
        int res= executeSelectInt(SqlScripts.GetAmtOfActivePlayers, arr);
        System.out.printf("Amount of active players:  " + res + "\n\n");
        return res;
    }

    /**
     * a method taht return results of recent game form database
     * @return
     */
    public List<String> getResults()
    {
        String[] arr= new String[0];
        return executeSelectPlayers(SqlScripts.getResult, arr);
    }


    /**
     * a method returns number of cards hold by chosen player
     * @param nick
     * @return
     */
    public    int getPlayerAmtOfCards(String nick)
    {
        return executeFunciton(SqlScripts.GetAmtOfCardsScript, nick);
    }


    /**
     * a method that returns cards on tablw stack
     * @return
     */
    public   List<UnoCard> selectTableStack()
    {
       return executeSelectCards(SqlScripts.TableStackViewScript);
    }

    /**
     * a method that return cards on mian stack
     * @return
     */
    public    List<UnoCard> selectMainStack()
    {
        return executeSelectCards(SqlScripts.MainStackViewScript);
    }

    /**
     * a mehhod return card hold by chosen player
     * @param nick
     * @return
     */
    public   List<UnoCard> selectFromHand(String nick)
    {
        return executeSelectCards(SqlScripts.SelectCardsFromHandScript, nick);
    }

    /**
     * a method that return order fo player from data base
     * @param nick
     * @return
     */
    public     List<String> selectOrderFromPlayer(String nick)
    {
        String[] arr={nick,nick,nick};
        return executeSelectPlayers(SqlScripts.SelectOrderForPlayer, arr);
    }

    /**
     * a method that executes sql procedur without variavles
     * @param sqlCode
     */
    private void executeProcedure(String sqlCode)
    {
        synchronized (this.semaphore) {
            try {
                Statement statement = connection.createStatement();
                statement.execute(sqlCode);
            } catch (SQLException e) {
                System.out.println(sqlCode + "--------------didint execute porpely---------------");
                e.printStackTrace();
            }
        }
    }

    /**
     * a method that validated nick from data base
     * @param nick
     * @return
     */
    public   boolean validateNick(String nick)
    {
        String[] array = new String[1];
        array[0]=nick;
        int amt = executeSelectInt(SqlScripts.validateNick, array);
        return amt==0;
    }

    /**
     * a method to validate password from database
     * @param nick
     * @param pass
     * @return
     */
    public  boolean validatePass(String nick, String pass)
    {
        String[] array = new String[2];
        array[0]=nick;
        array[1]=pass;
        int amt = executeSelectInt(SqlScripts.validatePass, array);
        return amt==1;
    }

    /**
     * a method that execute sql procedute with undetrimned amount of String varaivbles
     * @param sqlCode
     * @param vars
     * @return
     */
      private boolean executeProcedure(String sqlCode, String[] vars)
    {
        synchronized (this.semaphore) {
            try {
                System.out.println("*********************************************************" + sqlCode + "*******************************************");

                PreparedStatement statement = connection.prepareStatement(sqlCode);
                for (int i = 1; i <= vars.length; i++) {
                    statement.setString(i, vars[i - 1]);
                }
                statement.executeQuery();
            } catch (SQLException e) {
                System.out.println(sqlCode + "--------------didint execute porpely---------------");
                return false;
                // e.printStackTrace();
            }
            return true;
        }
    }

    /**
     * a method that executes sql procedure with 2 varaibles
     * @param sqlCode
     * @param var
     * @param numb
     */
     private void executeProcedure(String sqlCode, String var, int numb )
    {
        synchronized (this.semaphore) {
            try {
                System.out.println("*********************************************************" + sqlCode + "," + var + "," + numb + "*******************************************");
                PreparedStatement statement = connection.prepareStatement(sqlCode);
                statement.setString(1, var);
                statement.setInt(2, numb);
                statement.executeQuery();
            } catch (SQLException e) {
                System.out.println(sqlCode + "--------------didint execute porpely---------------");
                // e.printStackTrace();
            }
        }
    }

    /**
     * a method taht execute data base funtion with one varialbe
     * @param sqlCode
     * @return
     */
    private int executeFunciton(String sqlCode )
    {
        synchronized (this.semaphore) {
            int result = -1;
            try {
                CallableStatement statement = connection.prepareCall(sqlCode);
                statement.registerOutParameter(1, Types.INTEGER);
                ResultSet resultSet = statement.executeQuery();
                result = statement.getInt(1);
            } catch (SQLException e) {
                System.out.println(sqlCode + "--------------didint execute porpely---------------");
                e.printStackTrace();
            }
            return result;
        }
    }

    /**
     * a methot that execute fucntion in data base with 2 varaibles
     * @param sqlCode
     * @param var
     * @return
     */
    private int executeFunciton(String sqlCode, String var)
    {
        synchronized (this.semaphore) {
            int result = -1;
            try {
                CallableStatement statement = connection.prepareCall(sqlCode);
                statement.registerOutParameter(1, Types.INTEGER);
                statement.setString(2, var);
                ResultSet resultSet = statement.executeQuery();
                result = statement.getInt(1);
            } catch (SQLException e) {
                System.out.println(sqlCode + "--------------didint execute porpely---------------");
                e.printStackTrace();
            }
            return result;
        }
    }

    /**
     * a mrthod that execute multipe sql scripts from an array
     * @param statemnt
     */
     private void executeArrayStatements(String [] statemnt)
     {
         synchronized (this.semaphore) {
             for (String sqlCode : statemnt) {
                 try {
                     Statement statement = connection.createStatement();
                     statement.execute(sqlCode);
                 } catch (SQLException e) {
                     System.out.println(sqlCode + "--------------didint execute porpely---------------");
                     e.printStackTrace();
                 }

             }
         }
     }


    /**
     * a method taht seect cards of selcted players
     * @param sqlCode
     * @return
     */
     private List<UnoCard> executeSelectCards(String sqlCode)
     {
         synchronized (this.semaphore) {
             List<UnoCard> cardStack = new ArrayList<UnoCard>();
             try {

                 Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sqlCode);
                 while (resultSet.next()) {
                     UnoCard unoCard = new UnoCard(resultSet);
                     cardStack.add(unoCard);
                 }
             } catch (SQLException e) {
                 System.out.println(sqlCode + "--------------didint execute porpely---------------");
                 e.printStackTrace();
             }
             return cardStack;
         }
     }

    /**
     * a method that execute sql scripts that returns list of cards
     * @param sqlCode
     * @param var
     * @return
     */
    private List<UnoCard> executeSelectCards(String sqlCode, String var)
    {
        synchronized (this.semaphore) {
            List<UnoCard> cardStack = new ArrayList<UnoCard>();
            try {

                PreparedStatement statement = connection.prepareStatement(sqlCode);
                statement.setString(1, var);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    UnoCard unoCard = new UnoCard(resultSet);
                    cardStack.add(unoCard);
                }
            } catch (SQLException e) {
                System.out.println(sqlCode + "--------------didint execute porpely---------------");
                e.printStackTrace();
            }
            return cardStack;
        }
    }

    /**
     * a method execues scripts that returns single integer
     * @param sqlCode
     * @param var
     * @return
     */
    private int executeSelectInt(String sqlCode, String[] var)
    {
        synchronized (this.semaphore) {
            int res=-1;
            try {

                PreparedStatement statement = connection.prepareStatement(sqlCode);
                int i = 1;
                for (String variable : var
                ) {
                    statement.setString(i, variable);
                    i++;
                }
                ResultSet resultSet = statement.executeQuery();
                resultSet.next();
                res=resultSet.getInt(1);
            } catch (SQLSyntaxErrorException e) {
                System.out.println("sqlcode: " + sqlCode);
                e.printStackTrace();
                System.exit(-1
                );
            } catch (SQLException e) {
                System.out.println(sqlCode + "--------------didint execute porpely---------------");
                e.printStackTrace();
            }
            if(res==-1)
            {
                System.out.println("ERRO GETING INT");
                System.exit(-1);
            }
            return res;
        }
    }

    /**
     * a method that return list of nicks in ranking
     * @return
     */

    public  List<String> getListofNicksRanking()
    {
        String[] var = {};
        List<String> nicks= executeSelectPlayers(SqlScripts.SelectNicksRankingInOrder, var);
        return nicks;
    }

    /**
     * a mthod that retunr list of amt of wins in rabking
     * @return
     */
    public  List<Integer> getAmtOfWinsRaning()
    {
        List<Integer> wins= executeSelectNumbers(SqlScripts.SelectWinsRankingInOrder);
        return wins;
    }

    /**
     * this method exectue sql script that returns list of player
     * @param sqlCode
     * @param var
     * @return
     */
     private List<String> executeSelectPlayers(String sqlCode, String[] var)
    {
        synchronized (this.semaphore) {
            List<String> players = new ArrayList<String>();
            try {

                PreparedStatement statement = connection.prepareStatement(sqlCode);
                int i = 1;
                for (String variable : var
                ) {
                    statement.setString(i, variable);
                    i++;
                }
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    players.add(resultSet.getString("NICK"));
                    System.out.println("******************" + resultSet.getString("NICK"));
                }
            } catch (SQLSyntaxErrorException e) {
                System.out.println("sqlcode: " + sqlCode);
                e.printStackTrace();
                System.exit(-1
                );
            } catch (SQLException e) {
                System.out.println(sqlCode + "--------------didint execute porpely---------------");
                e.printStackTrace();
            }

            return players;
        }
    }

    /**
     * a method that executes sql scripts that returns numbers
     * @param sqlCode
     * @return
     */
    private List<Integer> executeSelectNumbers(String sqlCode)
    {
        synchronized (this.semaphore) {
            List<Integer> numbers = new ArrayList<Integer>();
            try {

                PreparedStatement statement = connection.prepareStatement(sqlCode);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    numbers.add(resultSet.getInt(1));
                   // System.out.println("******************" + resultSet.getString("NICK"));
                }
            } catch (SQLSyntaxErrorException e) {
                System.out.println("sqlcode: " + sqlCode);
                e.printStackTrace();
                System.exit(-1
                );
            } catch (SQLException e) {
                System.out.println(sqlCode + "--------------didint execute porpely---------------");
                e.printStackTrace();
            }

            return numbers;
        }
    }



}
