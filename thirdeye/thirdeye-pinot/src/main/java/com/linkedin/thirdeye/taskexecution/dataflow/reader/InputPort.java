package com.linkedin.thirdeye.taskexecution.dataflow.reader;

import com.linkedin.thirdeye.taskexecution.operator.Operator;

public interface InputPort<T> {
  void setOperator(Operator operator);

  Operator getOperator();

  void initializeReader(Reader<T> reader);

  Reader<T> getReader();
}
