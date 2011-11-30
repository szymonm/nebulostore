package pl.edu.uw.mimuw.nebulostore.replicator.tests;

import pl.edu.uw.mimuw.nebulostore.appcore.DataFile;

/**
 * @author szymonmatejczyk
 */
public class SimpleStringFile extends DataFile {
  private static final long serialVersionUID = -2676252330563944892L;
  private String string_;

  public SimpleStringFile(String string) {
    super();
    string_ = string;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime + ((string_ == null) ? 0 : string_.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    SimpleStringFile other = (SimpleStringFile) obj;
    if (string_ == null) {
      if (other.string_ != null)
        return false;
    } else if (!string_.equals(other.string_))
      return false;
    return true;
  }

}
