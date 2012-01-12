package org.nebulostore.query.language.interpreter;

public class DQLInterpreter {

  public DQLInterpreter() {
    // TODO: Init libraries
  }

  public void prepareQuery(String query, String sender) {
    // TODO: Proper address support
  }

  public IntepreterState runGather(PreparedQuery query, IntepreterState state) {

    // TODO : prrzemyśleć, jak powinna wyglądać struktura, tak, żeby
    // można było pospać na chwilę między FORWARD a REDUCE
    return state;
  }

  public IntepreterState runForward(PreparedQuery query, IntepreterState state) {
    // Tutaj pewnie bierzemy jakąś funckję klasy, którą dostaliśmy w
    // konstruktorze, budujemy
    // message sieciowy i wysyłamy
    return state;
  }

  public IntepreterState runReduce(PreparedQuery query, IntepreterState state) {
    // Tutaj wysyłamy do ziomka, który nam odpowiedział, kiedy tylko state się
    // updateuje, że wszystkie odpowiedzi doszły, lub zaliczyliśmy timeout
    return state;
  }

}
