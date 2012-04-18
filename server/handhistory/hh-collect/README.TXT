This is a Firebase service that implements the HandHistoryCollectorService (which is located in the poker-game module).

It builds a hand history representation given the data from the game. When it comes to actually storing the hand
history, it will try to find a Firebase service of the type HandHistoryPersistenceService. If no such service is found,
it will use the JsonHandHistoryLogger, which will log the hand history as JSON in a log file.