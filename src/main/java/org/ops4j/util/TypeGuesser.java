package org.ops4j.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.ops4j.OpData;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class TypeGuesser
{
  public enum InferredType {
    INTEGER, DOUBLE, STRING
  }
  
  public static Map<String, InferredType> guessTypes(List<ObjectNode> data)
  {
    Map<String, InferredType> guess = new HashMap<String, InferredType>();

    for (ObjectNode node : data)
    {
      if (node != null)
      {
        Iterator<String> it = node.fieldNames();
        while (it.hasNext())
        {
          String fieldName = it.next();

          InferredType curGuess = InferredType.INTEGER;
          if (guess.containsKey(fieldName))
          {
            curGuess = guess.get(fieldName);
          }

          JsonNode field = node.get(fieldName);
          switch (curGuess)
          {
            case STRING:
            {
              break;
            }
            case DOUBLE:
            {
              if (!field.isDouble())
              {
                // System.out.println(
                // "FIELD-NAME: " + fieldName + " = '" + field.asText() + "'"
                // + " is not a double. Casting to STRING.");
                curGuess = InferredType.STRING;
              }
              break;
            }
            case INTEGER:
            {
              if (!field.isInt())
              {
                if (field.isDouble())
                {
                  // System.out.println(
                  // "FIELD-NAME: " + fieldName + " = '" + field.asText() + "'"
                  // + " is not an integer. Casting to DOUBLE.");
                  curGuess = InferredType.DOUBLE;
                }
                else
                {
                  // System.out.println("FIELD-NAME: " + fieldName + " = '"
                  // + field.asText() + "'"
                  // + " is not an integer or a double. Casting to STRING.");
                  curGuess = InferredType.STRING;
                }
              }
              break;
            }
            default:
            {
              curGuess = InferredType.STRING;
            }
          }
          guess.put(fieldName, curGuess);
        }
      }
    }
    return guess;
  }
}
