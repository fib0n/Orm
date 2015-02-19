hh.homework: hibernate and jdbc example
==========
1. 2 таблицы - Clubs, Players
2. 2 DAO, в каждом get, addOrUpdate, delete
3. 2 сервиса - ClubsService обращается к PlayerService в транзакции
4. Запуск:
  > mvn compile

  > mvn test

  > mvn exec:java -Dexec.mainClass="hh.homework.transactionexample.Main" -Dexec.args="reinit" (reinit - запуск с пересозданием бд)
