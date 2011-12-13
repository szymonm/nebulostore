package org.nebulostore.query.dql.data.functions;

import java.util.List;

import org.nebulostore.query.dql.data.access.AccessLevelChange;

import com.google.common.base.Function;

/**
 * @author Marcin Walas
 */
abstract public class DQLFunction<F, T> implements Function<F, T> {

  protected List<AccessLevelChange> supportedChanges_;

  public DQLFunction(List<AccessLevelChange> supportedChanges) {
    supportedChanges_ = supportedChanges;
  }
}
