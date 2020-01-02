package com.gtcom.janusimport.schema.entity;


import com.gtcom.janusimport.schema.enuminfo.DataType;
import com.gtcom.janusimport.schema.enuminfo.Mapping;
import org.janusgraph.core.Cardinality;

import java.io.Serializable;

/**
 * schema中IndexPropertyKey实体信息.
 * @author GH
 * @version 2018-07-25
 *
 */
public class IndexPropertyKey implements Serializable {

  private static final long serialVersionUID = 8944255584746876120L;

  private String name;

  private DataType type;

  private Mapping mapping;

  private String description;

  private Cardinality cardinality;


  public IndexPropertyKey() {}

  public IndexPropertyKey(String name) {
    this.name = name;
  }

  public IndexPropertyKey(String name, DataType type) {
    this.name = name;
    this.type = type;
  }

  public IndexPropertyKey(String name, Mapping mapping) {
    this.name = name;
    this.mapping = mapping;
  }

  public IndexPropertyKey(String name, DataType type, Mapping mapping) {
    this.name = name;
    this.type = type;
    this.mapping = mapping;
  }

  public IndexPropertyKey(String name, DataType type, Mapping mapping, String description) {
    this.name = name;
    this.type = type;
    this.mapping = mapping;
    this.description = description;
  }

  public IndexPropertyKey(String name, DataType type, Mapping mapping, String description, Cardinality cardinality) {
    this.name = name;
    this.type = type;
    this.mapping = mapping;
    this.description = description;
    this.cardinality = cardinality;
  }

  public Cardinality getCardinality() {
    return cardinality;
  }

  public void setCardinality(Cardinality cardinality) {
    this.cardinality = cardinality;
  }


  @Override
  public String toString() {
    return "IndexPropertyKey{" +
            "name='" + name + '\'' +
            ", type=" + type +
            ", mapping=" + mapping +
            ", description='" + description + '\'' +
            ", cardinality=" + cardinality +
            '}';
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public DataType getType() {
    return type;
  }

  public void setType(DataType type) {
    this.type = type;
  }

  public Mapping getMapping() {
    return mapping;
  }

  public void setMapping(Mapping mapping) {
    this.mapping = mapping;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

}
