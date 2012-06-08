package org.nebulostore.query.trivial;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import org.nebulostore.addressing.AppKey;
import org.nebulostore.query.QueryDescription;
import org.nebulostore.query.QueryTestServer;

public class TrivialQueryTestServer extends QueryTestServer {

  public TrivialQueryTestServer() {
    super(5, 38, 38, 360, 60, "Trivial Query Test", false, "Trivial Query Test",
        null, 10);

    Map<AppKey, Map<Integer, QueryDescription>> queryTests = new HashMap<AppKey, Map<Integer, QueryDescription>>();

    String query = " GATHER"
        + "  LET peerAge = IF(LEAF_EXECUTION, LOAD_NOISE(\"peerData.xml\" , \"/peerData/age\", FALSE),  LOAD (\"peerData.xml\" , \"/peerData/age\", FALSE))  "
        + " FORWARD"
        + " TO"
        + "  LOAD(\"peerData.xml\", \"/peerData/friends/friend\", TRUE)"
        + " REDUCE"
        + "  SUM(APPEND(peerAge,DQL_RESULTS))";


    String query2 = "GATHER "
        + " LET nextHop = LOAD(\"peerData.xml\", \"/peerData/friends/friend\", TRUE)"
        + " FORWARD"
        + " TO"
        + "  nextHop "
        + " REDUCE "
        + "LENGTH(DQL_RESULTS) + 1";

    String query3 =
        "   GATHER"
            + "  LET likes = LOAD_NOISE(\"peerData.xml\" , \"/peerData/likes/like\", TRUE) "
            + "  LET likesNumber = LENGTH(likes) IS PUBLIC_MY AS INTEGER "
            + "  LET name = LOAD(\"peerData.xml\" , \"/peerData/name-public\", FALSE) "
            + " FORWARD"
            + " TO"
            + "  LOAD(\"peerData.xml\", \"/peerData/friends/friend\", TRUE) "
            + " REDUCE "
            + "  IF (likesNumber > 2, "
            + "         APPEND(name, FOLDL(LAMBDA resultList, list : (CONCAT(resultList, list)), CREATE_LIST(), DQL_RESULTS)), "
            + "         FOLDL(LAMBDA resultList,list : (CONCAT(resultList, list)), CREATE_LIST(), DQL_RESULTS)"
            + "     )";

    String query4 =
        "GATHER "
            + "LET userFriends = "
            + "      LOAD(\"peerData.xml\", \"/peerData/friends/friend\", TRUE)"
            + "   IS PRIVATE_MY AS LIST < INTEGER > "
            + "LET name = LOAD(\"peerData.xml\" , \"/peerData/name-public\", FALSE) "
            + "   IS PUBLIC_MY AS STRING "
            + "LET sports = LOAD(\"peerData.xml\", \"/peerData/sports\", FALSE ) "
            + "   IS PRIVATE_COND "
            + "LET obfuscate = LENGTH(LOAD_NOISE(\"peerData.xml\", \"/peerData/friends/friend\", TRUE)) "
            + "   IS PUBLIC_MY "
            + "LET numberOfLikes = LENGTH(LOAD_NOISE(\"peerData.xml\", \"/peerData/likes/like\", TRUE)) "
            + "   IS PUBLIC_MY "
            + "LET obfuscateTwo = numberOfLikes / numberOfLikes "
            + "   IS PUBLIC_MY "
            + "FORWARD "
            + "TO "
            + " userFriends "
            + "REDUCE "
            + "   IF(sports*obfuscate >= obfuscateTwo, "
            + "         APPEND(name, FOLDL(LAMBDA resultList, list : (CONCAT(resultList, list)), CREATE_LIST(), DQL_RESULTS)), "
            + "         FOLDL(LAMBDA resultList,list : (CONCAT(resultList, list)), CREATE_LIST(), DQL_RESULTS)"
            + "     )";
    Map<Integer, QueryDescription> tests1 = new HashMap<Integer, QueryDescription>();
    tests1.put(0, new QueryDescription(query, 3));
    tests1.put(1, new QueryDescription(query2, 3));
    tests1.put(2, new QueryDescription(query3, 3));
    tests1.put(3, new QueryDescription(query4, 3));

    Map<Integer, QueryDescription> tests2 = new HashMap<Integer, QueryDescription>();
    tests2.put(0, new QueryDescription(query, 3));
    tests2.put(1, new QueryDescription(query2, 3));
    tests2.put(2, new QueryDescription(query3, 3));
    tests2.put(3, new QueryDescription(query4, 3));

    for (int appKey = 1; appKey <= 40; appKey++) {
      queryTests.put(new AppKey(BigInteger.valueOf(appKey)), tests1);
    }
    //queryTests.put(new AppKey(BigInteger.valueOf(1)), tests1);
    //queryTests.put(new AppKey(BigInteger.valueOf(2)), tests2);
    setQueryTests(queryTests);
  }

}
