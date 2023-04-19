package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class DataBaseMangaer {


    private Connection connection;
    private String dataBasePort;
    private String dataBaseAdres;
    private String dataBaseName;
    private String dataBaseUserName;
    private String dataBasePass;
    public void setDataBaseName(String dataBaseName) {
        this.dataBaseName = dataBaseName;
    }

    public void setDataBasePort(String dataBasePort) {
        this.dataBasePort = dataBasePort;
    }

    public void setDataBaseUserName(String dataBaseUserName) {
        this.dataBaseUserName = dataBaseUserName;
    }

    public void setDataBasePass(String dataBasePass) {
        this.dataBasePass = dataBasePass;
    }

    public void setDataBaseAdres(String dataBaseAdres) {
        this.dataBaseAdres = dataBaseAdres;
    }

    private final Object semaphore=new Object();

    DataBaseMangaer()
    {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public DataBaseMangaer(String dataBaseAdres,String dataBasePort, String dataBaseName, String dataBaseUserName, String dataBasePass) {
        this.dataBasePort = dataBasePort;
        this.dataBaseAdres = dataBaseAdres;
        this.dataBaseName = dataBaseName;
        this.dataBaseUserName = dataBaseUserName;
        this.dataBasePass = dataBasePass;
    }

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


    private void createTables()
    {
        executeArrayStatements(SqlScripts.CreateTablecSripts);
    }

    private void dropTables()
    {
        executeArrayStatements(SqlScripts.DropTablecSripts);
    }

    private void deleteAll()
    {
        executeArrayStatements(SqlScripts.DeleteAllScripts);
    }

    private void createViews()
    {
        executeArrayStatements(SqlScripts.CreateViewsScripts);
    }

    private void createSeq()
    {
        executeArrayStatements(SqlScripts.CreateSequencesScripts);
    }
    private void dropSeq()
    {
        executeArrayStatements(SqlScripts.DropSequencesScripts);
    }

    private void createFunctions()
    {
        executeArrayStatements(SqlScripts.CreateFunctions);
    }

    private void createProcedures()
    {
        executeArrayStatements(SqlScripts.CreateProceduresScripts);
    }

    public boolean addPlayer(String nick, String pass)
    {
        String[] arr={nick,pass};
        boolean res= executeProcedure(SqlScripts.AddPlayerScript,arr);
        return res;
    }

    private void createBaseCards()
    {
        executeProcedure(SqlScripts.FillBaseCardsScripts);
    }

    public void createNewGame(String nick)
    {
        String[] arr={nick};
        executeProcedure(SqlScripts.CreateNewGameScripts,arr);
    }

    public void addPlayerToGame(String nick)
    {
        String[] arr={nick};
        executeProcedure(SqlScripts.AddPlayerToGameScript,arr);
    }

    public void drawCard(String nick)
    {
        String[] arr={nick};
        executeProcedure(SqlScripts.DrawCardScript,arr);
    }

    public void preapreDeck()
    {
        executeProcedure(SqlScripts.PreapareDeckScript);
    }

    public void dealCards()
    {
        executeProcedure(SqlScripts.DealCardScript);
    }

    public void playCard(String nick, int pos)
    {
        executeProcedure(SqlScripts.PlayCardScript,nick,pos);
    }

    public void reshuffleDeck()
    {
        executeProcedure(SqlScripts.ReshuffleDeck);
    }

    public    void surrender(String nick)
    {
        String[] arr={nick};
        executeProcedure(SqlScripts.SurrenderScript,arr);
    }
    public    void setRank(String nick)
    {
        String[] arr={nick};
        executeProcedure(SqlScripts.SetRankScript,arr);
    }


    public  int getNmOnStack()
    {
        return executeFunciton(SqlScripts.GetCardAmtOnStack);
    }

    public  int getAmtInHand(String nick)
    {
        String[] arr={nick};
        return executeSelectInt(SqlScripts.GetAMtOfCardInHands,arr );
    }

    public    int getAmtActivePlayers()
    {
        String[] arr=new String[0];
        int res= executeSelectInt(SqlScripts.GetAmtOfActivePlayers, arr);
        System.out.printf("Amount of active players:  " + res + "\n\n");
        return res;
    }
    public List<String> getResults()
    {
        String[] arr= new String[0];
        return executeSelectPlayers(SqlScripts.getResult, arr);
    }



    public    int getPlayerAmtOfCards(String nick)
    {
        return executeFunciton(SqlScripts.GetAmtOfCardsScript, nick);
    }



    public   List<UnoCard> selectTableStack()
    {
       return executeSelectCards(SqlScripts.TableStackViewScript);
    }

    public    List<UnoCard> selectMainStack()
    {
        return executeSelectCards(SqlScripts.MainStackViewScript);
    }

    public   List<UnoCard> selectFromHand(String nick)
    {
        return executeSelectCards(SqlScripts.SelectCardsFromHandScript, nick);
    }

    public     List<String> selectOrderFromPlayer(String nick)
    {
        String[] arr={nick,nick,nick};
        return executeSelectPlayers(SqlScripts.SelectOrderForPlayer, arr);
    }

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

    public   boolean validateNick(String nick)
    {
        String[] array = new String[1];
        array[0]=nick;
        int amt = executeSelectInt(SqlScripts.validateNick, array);
        return amt==0;
    }

    public  boolean validatePass(String nick, String pass)
    {
        String[] array = new String[2];
        array[0]=nick;
        array[1]=pass;
        int amt = executeSelectInt(SqlScripts.validatePass, array);
        return amt==1;
    }

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


    public  List<String> getListofNicksRanking()
    {
        String[] var = {};
        List<String> nicks= executeSelectPlayers(SqlScripts.SelectNicksRankingInOrder, var);
        return nicks;
    }

    public  List<Integer> getAmtOfWinsRaning()
    {
        List<Integer> wins= executeSelectNumbers(SqlScripts.SelectWinsRankingInOrder);
        return wins;
    }

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
