package gy.fraud

import com.datastax.spark.connector.cql.CassandraConnector
import org.apache.spark.sql.ForeachWriter

class CassandraSinkForeach(connector: CassandraConnector, namespace: String, table: String)
  extends ForeachWriter[org.apache.spark.sql.Row] {

  def process(record: org.apache.spark.sql.Row): Unit = {
    connector.withSessionDo(session =>
      session.execute(s"""
       insert into ${namespace}.${table} (ip, type, count)
       values('${record(0)}', '${record(1)}', '${record(2)}')""")
    )
  }

  override def close(errorOrNull: Throwable): Unit = ()
  override def open(partitionId: Long, epochId: Long): Boolean = true
}
