package fr.convergence.proddoc.model.lib.obj

class PayloadDebezium {
    var after: Any? = null
    var source: SourceDebezium? = null
}

class SourceDebezium {
    var version: String? = null
    var connector: String? = null
    var name: String? = null
    var ts_ms: String? = null
    var snapshot: String? = null
    var db: String? = null
    var schema: String? = null
    var table: String? = null
    var txId: String? = null
    var lsn: String? = null
    var xmin: String? = null
}