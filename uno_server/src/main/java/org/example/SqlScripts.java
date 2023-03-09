package org.example;

public class SqlScripts {

    static String TableStackViewScript=
    "SELECT * FROM Table_stack_view";

    static String MainStackViewScript=
            "SELECT * FROM stack_view";

    static String AddPlayerScript =
            "{call ADD_PLAYER(?)}";

    static String FillBaseCardsScripts=
            "{call FILL_BASE_CARDS}";
    static String CreateNewGameScripts=
            "{call CREATE_NEW_GAME(?)}";

    static String AddPlayerToGameScript =
            "{call ADD_PLAYER_TO_GAME(?)}";

    static String PreapareDeckScript=
            "{call PREPEARE_DECK}";
    static String DealCardScript=
            "{call DEAL_CARDS}";
    static String PlayCardScript=
            "{call PLAY_CARD(?,?)}";

    static String ReshuffleDeck=
            "{call RESHUFFLE_STACK}";
    static String DrawCardScript=
            "{call DRAW_CARD(?)}";

    static String SurrenderScript=
            "{call SURRENDER(?)}";

    static String SetRankScript=
            "{call SET_RANK(?)}";

    static String ClearGameSrript=
            "{call CLEAR_GAME}";


    static String GetPlayerCountScript=
    "{? = call GET_PLAYER_COUNT}";

    static String GetNumberOnTableScript=
            "{? = call  GET_NUMBER_ON_TABLE }";

    static String GetAmtOfCardsScript=
            "{? = call  GET_AMT_OF_CARD(?) }";

    static String ValidateHand=
            "{? = call VALIDATE_HAND(?)}";

    static String SelectCardsFromHandScript=
            "SELECT  ACTIVE_CARD_PLACES.CARDS_ID, ACTIVE_CARD_PLACES.POSITION, COLOR,TYPE, NUMB  FROM ACTIVE_CARD_PLACES, CARDS "+
                    "WHERE ACTIVE_CARD_PLACES.NICK= ? AND "+
                    "ACTIVE_CARD_PLACES.CARDS_ID=CARDS.CARDS_ID "+
                    "ORDER BY POSITION DESC ";


    static String SelectOrderForPlayer=
            "SELECT * FROM( "+
                    "SELECT * FROM( "+
                    " SELECT NICK, turn- (SELECT MAX(TURN) turn FROM PLAYER_ORDER "+
                    "    WHERE TURN>( "+
                    "        SELECT TURN FROM PLAYER_ORDER "+
                    "        WHERE nick= ? "+
                    "        )) turn FROM PLAYER_ORDER "+
                    "    WHERE TURN>( "+
                    "        SELECT TURN FROM PLAYER_ORDER "+
                    "        WHERE nick= ? "+
                    "        ) "+
                    "    ) "+
                    "UNION ( "+
                    "    SELECt NICK, turn FROM PLAYER_ORDER "+
                    "    WHERE TURN<( "+
                    "        SELECT TURN FROM PLAYER_ORDER "+
                    "        WHERE nick= ? "+
                    "    ) "+
                    "     "+
                    ") "+
                    ")ORDER BY TURN ";

    static String[] CreateTablecSripts={
            "CREATE TABLE PLAYERS( "+
                    "NICK VARCHAR(30) PRIMARY KEY "+
                    ") "+
                    "",
            "CREATE TABLE GAMES( "+
                    "NICK VARCHAR(30) CONSTRAINT games_nick_fk REFERENCES PLAYERS(NICK), "+
                    "GAMES_ID NUMBER(6) , "+
                    "RANK NUMBER(1), "+
                    "PRIMARY KEY(NICK, GAMES_ID) "+
                    ") "+
                    "",
            "CREATE TABLE CARDS( "+
                    "CARDS_ID  NUMBER(6) PRIMARY KEY, "+
                    "COLOR VARCHAR(10), "+
                    "TYPE VARCHAR(10), "+
                    "NUMB NUMBER(2) "+
                    ") "+
                    "",
            "CREATE TABLE ACTIVE_CARD_PLACES( "+
                    "CARDS_ID NUMBER(6) PRIMARY KEY CONSTRAINT card_id_fk REFERENCES CARDS(CARDS_ID), "+
                    "PLACE_TYPE VARCHAR(10), "+
                    "NICK VARCHAR(30) CONSTRAINT cards_nick_fk REFERENCES PLAYERS(NICK), "+
                    "POSITION INTEGER "+
                    ") "+
                    ""
    };
    static String[] DropTablecSripts={
            "DROP TABLE ACTIVE_CARD_PLACES "+
                "",
                "DROP TABLE CARDS "+
                        "",
                "DROP TABLE GAMES "+
                        "",
                "DROP TABLE PLAYERS "
    };

    static String[] DeleteAllScripts=
    {
        "DELETE FROM ACTIVE_CARD_PLACES"+
                "",
                "DELETE FROM  CARDS "+
                        "",
                "DELETE FROM  GAMES "+
                        "",
                "DELETE FROM  PLAYERS "+
                        ""
    };


    static String[] CreateViewsScripts=
            {
                    "CREATE OR REPLACE VIEW Table_stack_view AS "+
                            "SELECT  ACTIVE_CARD_PLACES.CARDS_ID, ACTIVE_CARD_PLACES.POSITION, COLOR,TYPE, NUMB  FROM ACTIVE_CARD_PLACES, CARDS "+
                            "WHERE ACTIVE_CARD_PLACES.PLACE_Type='TABLE' AND "+
                            "ACTIVE_CARD_PLACES.CARDS_ID=CARDS.CARDS_ID "+
                            "ORDER BY POSITION DESC "+
                            "",
                    "CREATE OR REPLACE VIEW stack_view AS "+
                            "SELECT  ACTIVE_CARD_PLACES.CARDS_ID, ACTIVE_CARD_PLACES.POSITION, COLOR,TYPE, NUMB  FROM ACTIVE_CARD_PLACES, CARDS "+
                            "WHERE ACTIVE_CARD_PLACES.PLACE_Type='STACK' AND "+
                            "ACTIVE_CARD_PLACES.CARDS_ID=CARDS.CARDS_ID "+
                            "ORDER BY POSITION DESC "+
                            "",
                    "CREATE OR REPLACE VIEW PLAYER_ORDER AS "+
                            "SELECT NICK, RANK() OVER(ORDER BY NICK) turn FROM GAMES "+
                            "WHERE GAMES_ID = GET_ACTIVE_GAME_ID "+
                            "ORDER BY NICK "+
                            ""
            };

    static String[] CreateSequencesScripts=
    {
        "CREATE SEQUENCE GAME_ID_SEQ"+
                "",
    };
    static String[] DropSequencesScripts=
            {
                    "Drop SEQUENCE GAME_ID_SEQ"+
                            "",
            };
    static String[] CreateFunctions=
    {
        "CREATE OR REPLACE FUNCTION GET_PLAYER_COUNT RETURN NUMBER "+
                "AS "+
                "amount NUMBER; "+
                "val NUMBER; "+
                "BEGIN "+
                "val:=GET_ACTIVE_GAME_ID; "+
                "SELECT COUNT(*) INTO amount "+
                "FROM GAMES  "+
                "WHERE GAMES_ID = val "+
                "GROUP BY GAMES_ID; "+
                "RETURN amount; "+
                "END; "+
                "",
                "CREATE OR REPLACE FUNCTION GET_ACTIVE_GAME_ID RETURN NUMBER "+
                        "AS "+
                        "val NUMBER; "+
                        "BEGIN "+
                        "SELECT MAX(GAMES_ID) INTO val FROM games; "+
                        "RETURN val; "+
                        "END; "+
                        "",
                "CREATE OR REPLACE FUNCTION  GET_NUMBER_ON_TABLE RETURN NUMBER "+
                        "AS "+
                        "val NUMBER(6); "+
                        "BEGIN "+
                        "SELECT max(Position) INTO val FROM ACTIVE_CARD_PLACES "+
                        "WHERE PLACE_TYPE='TABLE'; "+
                        "RETURN val; "+
                        "END; "+
                        "",
                "CREATE OR REPLACE FUNCTION GET_AMT_OF_CARD (nick_var VARCHAR) RETURN NUMBER AS "+
                        "val NUMBER (6); "+
                        "BEGIN "+
                        "SELECT count(*) INTO val FROM ACTIVE_CARD_PLACES "+
                        "WHERE NICK=nick_var; "+
                        "RETURN val; "+
                        "END; "+
                        "",
                "CREATE OR REPLACE FUNCTION GET_CURR_LOWEST_PLACE RETURN NUMBER AS "+
                        "val NUMBER (6); "+
                        "BEGIN "+
                        "SELECT max(RANK) INTO val FROM GAMES WHERE "+
                        "GAMES_ID = GET_ACTIVE_GAME_ID "+
                        "; "+
                        "RETURN val; "+
                        "END;",
            "CREATE OR REPLACE FUNCTION VALIDATE_HAND (nick_var VARCHAR) RETURN NUMBER AS "+
                    "CURSOR HAND_STACK IS "+
                    "SELECT COLOR,TYPE,NUMB FROM ACTIVE_CARD_PLACES, CARDS "+
                    "WHERE NICK = nick_var  AND "+
                    "ACTIVE_CARD_PLACES.CARDS_ID = CARDS.CARDS_ID "+
                    "ORDER BY POSITION "+
                    "; "+
                    "CARD_IN_HAND HAND_STACK%ROWTYPE; "+
                    "CARD_ON_TOP HAND_STACK%ROWTYPE; "+
                    "res NUMBER:=1; "+
                    "BEGIN  "+
                    "SELECT  COLOR,TYPE,NUMB INTO CARD_ON_TOP FROM ACTIVE_CARD_PLACES, CARDS "+
                    "where PLACE_TYPE='TABLE' "+
                    "AND ACTIVE_CARD_PLACES.CARDS_ID=CARDS.CARDS_ID "+
                    "AND POSITION = ( "+
                    "SELECT max(POSITION) FROM ACTIVE_CARD_PLACES "+
                    "where PLACE_TYPE='TABLE'); "+
                    "FOR card IN HAND_STACK "+
                    "LOOP "+
                    "IF(card.COLOR =  'BLACK') THEN "+
                    "res:=0; "+
                    "END IF; "+
                    "IF(card.TYPE = CARD_ON_TOP.TYPE OR card.COLOR=CARD_ON_TOP.COLOR)THEN "+
                    "res:=0; "+
                    "END IF; "+
                    "IF(card.NUMB IS NOT NULL AND card.NUMB=CARD_ON_TOP.NUMB)THEN "+
                    "res:=0; "+
                    "END IF; "+
                    "END lOOP; "+
                    "RETURN res; "+
                    "END; "
    };



    static String[] CreateProceduresScripts=
    {
        "CREATE OR REPLACE PROCEDURE ADD_CARD(ID_var  NUMBER, COLOR_var VARCHAR, TYPE_VAR VARCHAR , NUMBER_VAR  NUMBER:=NULL) "+
                "AS "+
                "BEGIN "+
                "INSERT INTO CARDS (cards_id, COLOR, TYPE, NUMB) "+
                "VALUES(ID_VAR, COLOR_VAR, TYPE_VAR, NUMBER_VAR); "+
                "END; "+
                "",
                "CREATE OR REPLACE PROCEDURE REORDER_STACK AS "+
                        "CURSOR STACK_CUR IS "+
                        "SELECT * FROM active_card_places "+
                        "WHERE PLACE_TYPE='STACK' "+
                        "ORDER BY active_card_places.POSITION DESC; "+
                        "iterator number(6):=1; "+
                        "BEGIN "+
                        "FOR card IN STACK_CUR "+
                        "LOOP "+
                        "UPDATE active_card_places "+
                        "SET POSITION= iterator "+
                        "WHERE CARDS_ID=card.CARDS_ID; "+
                        "iterator:=iterator+1; "+
                        "END LOOP; "+
                        "END; "+
                        "",
                "CREATE OR REPLACE PROCEDURE REORDER_HAND (nick_var VARCHAR) AS "+
                        "CURSOR hand_cur IS "+
                        "SELECT * FROM active_card_places "+
                        "WHERE Nick=nick_var "+
                        "ORDER BY active_card_places.POSITION DESC; "+
                        "iterator number(6):=1; "+
                        "BEGIN "+
                        "FOR card IN hand_cur "+
                        "LOOP "+
                        "UPDATE active_card_places "+
                        "SET POSITION= iterator "+
                        "WHERE CARDS_ID=card.CARDS_ID; "+
                        "iterator:=iterator+1; "+
                        "END LOOP; "+
                        "END; "+
                        "",
                "CREATE OR REPLACE PROCEDURE FILL_BASE_CARDS AS "+
                        "ID_COUNTER INT:=1; "+
                        "COLOR_var VARCHAR(10); "+
                        "iterator_1 NUMBER(3); "+
                        "iterator_2  NUMBER(3); "+
                        "iterator_3  NUMBER(3); "+
                        "BEGIN "+
                        "DELETE FROM CARDS; "+
                        "ADD_CARD(ID_COUNTER,'GREEN','REGULAR',0); "+
                        "ID_COUNTER :=ID_COUNTER+1; "+
                        "ADD_CARD(ID_COUNTER,'RED','REGULAR',0); "+
                        "ID_COUNTER :=ID_COUNTER+1; "+
                        "ADD_CARD(ID_COUNTER,'BLUE','REGULAR',0); "+
                        "ID_COUNTER :=ID_COUNTER+1; "+
                        "ADD_CARD(ID_COUNTER,'YELLOW','REGULAR',0); "+
                        "ID_COUNTER :=ID_COUNTER+1; "+
                        "FOR iterator_3 IN 1..2 "+
                        "LOOP  "+
                        "FOR iterator_1 IN 1..4 "+
                        "LOOP  "+
                        "IF iterator_1 = 1 THEN  "+
                        "  COLOR_var := 'RED'; "+
                        "ELSIF iterator_1 = 2 THEN  "+
                        "  COLOR_var := 'GREEN'; "+
                        "ELSIF iterator_1 = 3 THEN  "+
                        "  COLOR_var := 'BLUE'; "+
                        "ELSIF iterator_1 = 4 THEN  "+
                        "  COLOR_var := 'YELLOW'; "+
                        "END IF; "+
                        "FOR iterator_2 IN 1..9  "+
                        "LOOP "+
                        "ADD_CARD(ID_COUNTER,COLOR_var,'REGULAR',iterator_2); "+
                        "ID_COUNTER :=ID_COUNTER+1; "+
                        "END LOOP; "+
                        "ADD_CARD(ID_COUNTER,COLOR_var,'BLOCK'); "+
                        "ID_COUNTER :=ID_COUNTER+1; "+
                        "ADD_CARD(ID_COUNTER,COLOR_var,'REVERSE'); "+
                        "ID_COUNTER :=ID_COUNTER+1; "+
                        "ADD_CARD(ID_COUNTER,COLOR_var,'PLUS2'); "+
                        "ID_COUNTER :=ID_COUNTER+1; "+
                        "END LOOP; "+
                        "FOR iterator_2 IN 1..2  "+
                        "LOOP "+
                        "ADD_CARD(ID_COUNTER,'BLACK','PLUS4'); "+
                        "ID_COUNTER :=ID_COUNTER+1; "+
                        "ADD_CARD(ID_COUNTER,'BLACK','COLOR'); "+
                        "ID_COUNTER :=ID_COUNTER+1; "+
                        "END LOOP; "+
                        "END LOOP; "+
                        "END; "+
                        "",
                "CREATE OR REPLACE PROCEDURE ADD_PLAYER(NICK_VAR VARCHAR) "+
                        "AS "+
                        "BEGIN "+
                        "INSERT INTO PLAYERS (NICK) "+
                        "VALUES(NICK_VAR); "+
                        "END; "+
                        "",
                "CREATE OR REPlACE PROCEDURE CREATE_NEW_GAME (NICK_VAR VARCHAR) "+
                        "AS "+
                        "BEGIN "+
                        "INSERT INTO GAMES (NICK, GAMES_ID, rank) "+
                        "VALUES (NICK_VAR, GAME_ID_SEQ.nextval, 0); "+
                        "END; "+
                        "",
                "CREATE OR REPlACE PROCEDURE ADD_PLAYER_TO_GAME (NICK_VAR VARCHAR) "+
                        "AS "+
                        "BEGIN "+
                        "INSERT INTO GAMES (NICK, GAMES_ID, rank) "+
                        "VALUES (NICK_VAR, GAME_ID_SEQ.currval, 0); "+
                        "END; "+
                        "",
                "CREATE OR REPLACE PROCEDURE PREPEARE_DECK "+
                        "AS "+
                        "CURSOR CARDS_CUR IS "+
                        "SELECT * FROM cards; "+
                        "CURSOR ACTIVE_CARDS_CUR IS "+
                        "SELECT * FROM active_card_places "+
                        "ORDER BY active_card_places.POSITION; "+
                        "position_val INTEGER:=1; "+
                        "BEGIN "+
                        "DELETE FROM active_card_places; "+
                        "FOR card in CARDS_CUR "+
                        "LOOP "+
                        "INSERT INTO active_card_places(CARDS_ID, PLACE_TYPE, NICK, POSITION) "+
                        "VALUES(card.CARDS_ID, 'STACK', NULL, DBMS_RANDOM.RANDOM); "+
                        "END LOOP; "+
                        "REORDER_STACK; "+
                        "END; "+
                        "",
                "CREATE OR REPLACE PROCEDURE PLAY_CARD(nick_var VARCHAR, postion_var NUMBER) "+
                        "AS "+
                        "val NUMBER(6); "+
                        "BEGIN "+
                        "SELECT GET_NUMBER_ON_TABLE INTO val FROM DUAL; "+
                        "UPDATE ACTIVE_CARD_PLACES "+
                        "SET "+
                        "PLACE_TYPE='TABLE', "+
                        "NICK=NULL, "+
                        "POSITION=val+1 "+
                        "WHERE "+
                        "NICK=nick_var  "+
                        "AND "+
                        "position=postion_var; "+
                        "REORDER_HAND(nick_var); "+
                        "END; "+
                        "",
                "CREATE OR REPLACE PROCEDURE DEAL_CARDS AS "+
                        "CURSOR ACTIVE_CARDS_CUR IS "+
                        "SELECT * FROM active_card_places "+
                        "ORDER BY active_card_places.POSITION DESC; "+
                        "CURSOR ACTIVE_PLAYERS IS "+
                        "SELECT NICK FROM GAMES "+
                        "WHERE GAMES_ID=GET_ACTIVE_GAME_ID; "+
                        "card ACTIVE_CARDS_CUR%ROWTYPE; "+
                        "iterator_1 NUMBER(6); "+
                        "iterator_2 NUMBER(6); "+
                        "card_type VARCHAR(10); "+
                        "limit NUMBER(6); "+
                        "BEGIN "+
                        "OPEN ACTIVE_CARDS_CUR; "+
                        "FOR person in ACTIVE_PLAYERS "+
                        "LOOP "+
                        "FOR iterator in 1..7 "+
                        "LOOP "+
                        "FETCH ACTIVE_CARDS_CUR INTO card; "+
                        "UPDATE active_card_places  "+
                        "SET "+
                        "PLACE_TYPE='HAND', "+
                        "NICK=person.NICK, "+
                        "position=iterator "+
                        "WHERE active_card_places.CARDS_ID =card.CARDS_ID; "+
                        "END LOOP; "+
                        "END LOOP; "+
                        "LOOP "+
                        "FETCH ACTIVE_CARDS_CUR INTO card; "+
                        "EXIT WHEN ACTIVE_CARDS_CUR%NOTFOUND;"+
                        "SELECT TYPE INTO card_type FROM CARDS "+
                        "    WHERE CARDS_ID=card.CARDS_ID; "+
                        "     "+
                        "    IF card_type='REGULAR' "+
                        "    THEN "+
                        "    UPDATE active_card_places  "+
                        "    SET "+
                        "    PLACE_TYPE='TABLE', "+
                        "    position=1 "+
                        "    WHERE active_card_places.CARDS_ID =card.CARDS_ID; "+
                        "        EXIT; "+
                        "    END IF; "+
                        "     "+
                        "END LOOP; "+
                        "REORDER_STACK; "+
                        "CLOSE ACTIVE_CARDS_CUR; "+
                        "END; "+
                        "",
                "CREATE OR REPLACE PROCEDURE DRAW_CARD (nick_var VARCHAR) "+
                        "AS "+
                        "CURSOR STACK_CUR IS "+
                        "SELECT * FROM active_card_places "+
                        "WHERE PLACE_TYPE='STACK' "+
                        "ORDER BY active_card_places.POSITION DESC; "+
                        "card STACK_CUR%ROWTYPE; "+
                        "LAST_NUM NUMBER(6); "+
                        "BEGIN "+
                        "SELECT MAX(position) INTO LAST_NUM FROM active_card_places "+
                        "WHERE NICK=nick_var; "+
                        "OPEN STACK_CUR; "+
                        "FETCH STACK_CUR INTO card; "+
                        "UPDATE active_card_places  "+
                        "SET "+
                        "PLACE_TYPE='HAND', "+
                        "NICK=nick_var, "+
                        "POSITION=LAST_NUM+1 "+
                        "WHERE active_card_places.CARDS_ID=card.CARDS_ID; "+
                        "CLOSE STACK_CUR; "+
                        "END; "+
                        "",
                "CREATE OR REPLACE PROCEDURE RESHUFFLE_STACK  "+
                        "AS "+
                        "CURSOR TABLE_PLIE_CUR IS "+
                        "SELECT * FROM ACTIVE_CARD_PLACES "+
                        "WHERE PLACE_TYPE='TABLE' "+
                        "ORDER BY ACTIVE_CARD_PLACES.POSITION DESC; "+
                        "card_place table_plie_cur%ROWTYPE; "+
                        "BEGIN  "+
                        "OPEN table_plie_cur; "+
                        "FETCH table_plie_cur INTO card_place; "+
                        "UPDATE ACTIVE_CARD_PLACES  "+
                        "SET "+
                        "POSITION =1 "+
                        "WHERE "+
                        "card_place.CARDS_ID = CARDS_ID; "+
                        "LOOP "+
                        "FETCH table_plie_cur INTO card_place; "+
                        "EXIT WHEN table_plie_cur%NOTFOUND; "+
                        "UPDATE ACTIVE_CARD_PLACES  "+
                        "SET "+
                        "POSITION = DBMS_RANDOM.RANDOM, "+
                        "PLACE_TYPE='STACK' "+
                        "WHERE "+
                        "card_place.CARDS_ID = CARDS_ID; "+
                        "END LOOP; "+
                        "CLOSE table_plie_cur; "+
                        "REORDER_STACK; "+
                        "END; "+
                        "",
                "CREATE OR REPLACE PROCEDURE SET_RANK(nick_var VARCHAR) "+
                        "AS "+
                        "place_var NUMBER(6); "+
                        "game_id_var NUMBER(6); "+
                        "BEGIN "+
                        "place_var:=GET_CURR_LOWEST_PLACE; "+
                        "game_id_var:=GET_ACTIVE_GAME_ID; "+
                        "UPDATE GAMES  "+
                        "SET "+
                        "RANK=place_var+1 "+
                        "WHERE "+
                        "GAMES_ID=game_id_var "+
                        "AND "+
                        "NICK=nick_var; "+
                        "UPDATE ACTIVE_CARD_PLACES "+
                        "SET "+
                        "POSITION=POSITION-2000, "+
                        "PLACE_TYPE='STACK', "+
                        "nick=NULL "+
                        "WHERE "+
                        "nick=nick_var; "+
                        "REORDER_STACK; "+
                        "END; "+
                        "",
                "CREATE OR REPLACE PROCEDURE SURRENDER(nick_var VARCHAR) "+
                        "AS "+
                        "game_id_var NUMBER(6); "+
                        "amt_of_players NUMBER(6); "+
                        "iter NUMBER(6); "+
                        "tmp NUMBER(6); "+
                        "new_rank NUMBER(6); "+
                        "BEGIN "+
                        "game_id_var:=GET_ACTIVE_GAME_ID; "+
                        "amt_of_players:=GET_PLAYER_COUNT; "+
                        "FOR iter IN REVERSE 1..amt_of_players  "+
                        "LOOP "+
                        "SELECT COUNT(*) INTO tmp FROM GAMES "+
                        "WHERE "+
                        "rank=iter; "+
                        "IF tmp=0 THEN "+
                        "new_rank:=iter; "+
                        "END IF; "+
                        "END LOOP; "+
                        "UPDATE GAMES  "+
                        "SET "+
                        "RANK=new_rank "+
                        "WHERE "+
                        "GAMES_ID=game_id_var "+
                        "AND "+
                        "NICK=nick_var; "+
                        "UPDATE ACTIVE_CARD_PLACES "+
                        "SET "+
                        "POSITION=POSITION-2000, "+
                        "PLACE_TYPE='STACK', "+
                        "nick=NULL "+
                        "WHERE "+
                        "nick=nick_var; "+
                        "REORDER_STACK; "+
                        "END; "+
                        "",
                "CREATE OR REPLACE PROCEDURE CLEAR_GAME "+
                        "AS "+
                        "BEGIN "+
                        "DELETE FROM ACTIVE_CARD_PLACES; "+
                        "END; "+
                        ""
    };




}
