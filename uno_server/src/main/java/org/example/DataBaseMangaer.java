package org.example;

import java.lang.reflect.Type;
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

    void connectWithDataBase()
    {
        try {
            this.connection= DriverManager.getConnection("jdbc:oracle:thin:@"+this.dataBaseAdres + ":"+ this.dataBasePort +":"+this.dataBaseName , this.dataBaseUserName, this.dataBasePass);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    void addPlayer(String nick)
    {
        String[] arr={nick};
        executeProcedure(SqlScripts.AddPlayerScript,arr);
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

    private void executeProcedure(String sqlCode, String[] vars)
    {
        try{
            PreparedStatement statement = connection.prepareStatement(sqlCode);
            for (int i=1;i<=vars.length;i++)
            {
                statement.setString(i,vars[i-1]);
            }
            statement.executeQuery();
        } catch (SQLException e) {
            System.out.println(sqlCode + "--------------didint execute porpely---------------");
           // e.printStackTrace();
        }
    }
    private void executeProcedure(String sqlCode, String var, int numb )
    {
        try{
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




}
