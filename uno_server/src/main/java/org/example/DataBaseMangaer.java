package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    boolean connectWithDataBase()
    {
        try {
            this.connection= DriverManager.getConnection("jdbc:oracle:thin:@"+this.dataBaseAdres + ":"+ this.dataBasePort +":"+this.dataBaseName , this.dataBaseUserName, this.dataBasePass);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    void resetDataBase()
    {
        this.dropSeq();
        this.dropTables();
        this.createSeq();
        this.createTables();
        this.createBaseCards();
    }

    void prepDataBase()
    {
        this.createFunctions();
        this.createProcedures();
        this.createViews();
    }

    boolean checkTable() throws SQLException {
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


    void createTables()
    {
        executeArrayStatements(SqlScripts.CreateTablecSripts);
    }

    void dropTables()
    {
        executeArrayStatements(SqlScripts.DropTablecSripts);
    }

    void deleteAll()
    {
        executeArrayStatements(SqlScripts.DeleteAllScripts);
    }

    void createViews()
    {
        executeArrayStatements(SqlScripts.CreateViewsScripts);
    }

    void createSeq()
    {
        executeArrayStatements(SqlScripts.CreateSequencesScripts);
    }
    void dropSeq()
    {
        executeArrayStatements(SqlScripts.DropSequencesScripts);
    }

    void createFunctions()
    {
        executeArrayStatements(SqlScripts.CreateFunctions);
    }

    void createProcedures()
    {
        executeArrayStatements(SqlScripts.CreateProceduresScripts);
    }

    boolean addPlayer(String nick)
    {
        String[] arr={nick};
        boolean res= executeProcedure(SqlScripts.AddPlayerScript,arr);
        return res;
    }

    void createBaseCards()
    {
        executeProcedure(SqlScripts.FillBaseCardsScripts);
    }

    void createNewGame(String nick)
    {
        String[] arr={nick};
        executeProcedure(SqlScripts.CreateNewGameScripts,arr);
    }

    void addPlayerToGame(String nick)
    {
        String[] arr={nick};
        executeProcedure(SqlScripts.AddPlayerToGameScript,arr);
    }

    void drawCard(String nick)
    {
        String[] arr={nick};
        executeProcedure(SqlScripts.DrawCardScript,arr);
    }

    void preapreDeck()
    {
        executeProcedure(SqlScripts.PreapareDeckScript);
    }

    void dealCards()
    {
        executeProcedure(SqlScripts.DealCardScript);
    }

    void playCard(String nick, int pos)
    {
        executeProcedure(SqlScripts.PlayCardScript,nick,pos);
    }

    void reshuffleDeck()
    {
        executeProcedure(SqlScripts.ReshuffleDeck);
    }

    void surrender(String nick)
    {
        String[] arr={nick};
        executeProcedure(SqlScripts.SurrenderScript,arr);
    }
    void setRank(String nick)
    {
        String[] arr={nick};
        executeProcedure(SqlScripts.SetRankScript,arr);
    }

    void clearGame()
    {
        executeProcedure(SqlScripts.ClearGameSrript);
    }

    int getPlayerCount()
    {
        return executeFunciton(SqlScripts.GetPlayerCountScript);
    }

    int getNumbOntheTable()
    {
        return executeFunciton(SqlScripts.GetNumberOnTableScript);
    }

    int getPlayerAmtOfCards(String nick)
    {
        return executeFunciton(SqlScripts.GetAmtOfCardsScript, nick);
    }

    List<UnoCard> selectTableStack()
    {
       return executeSelectCards(SqlScripts.TableStackViewScript);
    }

    List<UnoCard> selectMainStack()
    {
        return executeSelectCards(SqlScripts.MainStackViewScript);
    }

    List<UnoCard> selectFromHand(String nick)
    {
        return executeSelectCards(SqlScripts.SelectCardsFromHandScript, nick);
    }

    List<String> selectOrderFromPlayer(String nick)
    {
        String[] arr={nick,nick,nick};
        return executeSelectPlayers(SqlScripts.SelectOrderForPlayer, arr);
    }

    private void executeProcedure(String sqlCode)
    {
            try{
                Statement statement = connection.createStatement();
                statement.execute(sqlCode);
            } catch (SQLException e) {
                System.out.println(sqlCode + "--------------didint execute porpely---------------");
                e.printStackTrace();
            }
    }

    private boolean executeProcedure(String sqlCode, String[] vars)
    {
        try{
            System.out.println("*********************************************************"+ sqlCode +"*******************************************" );

            PreparedStatement statement = connection.prepareStatement(sqlCode);
            for (int i=1;i<=vars.length;i++)
            {
                statement.setString(i,vars[i-1]);
            }
            statement.executeQuery();
        } catch (SQLException e) {
            System.out.println(sqlCode + "--------------didint execute porpely---------------");
            return false;
           // e.printStackTrace();
        }
        return true;
    }
    private void executeProcedure(String sqlCode, String var, int numb )
    {
        try{
            System.out.println("*********************************************************"+ sqlCode +  "," +var + "," + numb + "*******************************************" );
            PreparedStatement statement = connection.prepareStatement(sqlCode);
            statement.setString(1,var);
            statement.setInt(2,numb);
            statement.executeQuery();
        } catch (SQLException e) {
            System.out.println(sqlCode + "--------------didint execute porpely---------------");
            // e.printStackTrace();
        }
    }

    private int executeFunciton(String sqlCode )
    {
        int result=-1;
        try{
            CallableStatement statement = connection.prepareCall(sqlCode);
            statement.registerOutParameter(1, Types.INTEGER);
            ResultSet resultSet=statement.executeQuery();
            result=(int)statement.getInt(1);
        } catch (SQLException e) {
            System.out.println(sqlCode + "--------------didint execute porpely---------------");
             e.printStackTrace();
        }
        return result;
    }

    private int executeFunciton(String sqlCode, String var)
    {
        int result=-1;
        try{
            CallableStatement statement = connection.prepareCall(sqlCode);
            statement.registerOutParameter(1, Types.INTEGER);
            statement.setString(2,var);
            ResultSet resultSet=statement.executeQuery();
            result=(int)statement.getInt(1);
        } catch (SQLException e) {
            System.out.println(sqlCode + "--------------didint execute porpely---------------");
            e.printStackTrace();
        }
        return result;
    }

     private void executeArrayStatements(String [] statemnt)
     {
         for (String sqlCode: statemnt) {
             try{
             Statement statement = connection.createStatement();
             statement.execute(sqlCode);
             } catch (SQLException e) {
                System.out.println(sqlCode + "--------------didint execute porpely---------------");
                e.printStackTrace();
             }

         }
     }


     private List<UnoCard> executeSelectCards(String sqlCode)
     {
         List<UnoCard> cardStack= new ArrayList<UnoCard>();
         try{

             Statement statement = connection.createStatement();
             ResultSet resultSet=statement.executeQuery(sqlCode);
             while (resultSet.next())
             {
                 UnoCard unoCard= new UnoCard(resultSet);
                 cardStack.add(unoCard);
             }
         } catch (SQLException e) {
             System.out.println(sqlCode + "--------------didint execute porpely---------------");
             e.printStackTrace();
         }
         return cardStack;
     }

    private List<UnoCard> executeSelectCards(String sqlCode, String var)
    {
        List<UnoCard> cardStack= new ArrayList<UnoCard>();
        try{

            PreparedStatement statement = connection.prepareStatement(sqlCode);
            statement.setString(1,var);
            ResultSet resultSet=statement.executeQuery();
            while (resultSet.next())
            {
                UnoCard unoCard= new UnoCard(resultSet);
                cardStack.add(unoCard);
            }
        } catch (SQLException e) {
            System.out.println(sqlCode + "--------------didint execute porpely---------------");
            e.printStackTrace();
        }
        return cardStack;
    }



    private List<String> executeSelectPlayers(String sqlCode, String[] var)
    {
        List<String> players= new ArrayList<String>();
        try{

            PreparedStatement statement = connection.prepareStatement(sqlCode);
            int i=1;
            for (String variable:var
                 ) {
                statement.setString(i,variable);
                i++;
            }
            ResultSet resultSet=statement.executeQuery();
            while (resultSet.next())
            {
                players.add(resultSet.getString("NICK"));
            }
        } catch (SQLException e) {
            System.out.println(sqlCode + "--------------didint execute porpely---------------");
            e.printStackTrace();
        }
        return players;
    }



}
