package nonabili.statusserviceserver.util.error

class CustomError(val reason: ErrorState): RuntimeException(reason.message)