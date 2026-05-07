package org.ops4j.smile.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.ops4j.OpData;
import org.ops4j.exception.OpsException;
import org.ops4j.util.TypeGuesser;
import org.ops4j.util.TypeGuesser.InferredType;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class SmileUtil
{
  public static double[][] x(List<OpData> data, List<String> features)
  {
    {
      // Calculate field names
      if (data.size() == 0)
      {
        return new double[0][0];
      }

      List<ObjectNode> objs = data.stream().map(opdata -> {
        try
        {
          return (ObjectNode) opdata.toJson();
        }
        catch(OpsException e)
        {
          return null;
        }
      }).collect(Collectors.toList());
      Map<String, InferredType> types = TypeGuesser.guessTypes(objs);
      Map<String, Map<String, Double>> encodings = new HashMap<>();
      double x[][];
      double encodedValue = 0.0;
      x = new double[data.size()][features.size()];

      int row = 0;
      for (ObjectNode node : objs)
      {
        int col = 0;
        for (String fieldName : features)
        {
          switch (types.get(fieldName))
          {
            case STRING:
            {
              if (!encodings.containsKey(fieldName))
              {
                encodings.put(fieldName, new HashMap<String, Double>());
                encodings.get(fieldName).put(node.get(fieldName).asText(),
                    encodedValue++);
              }
              else if (!encodings.get(fieldName)
                  .containsKey(node.get(fieldName).asText()))
              {
                encodings.get(fieldName).put(node.get(fieldName).asText(),
                    encodedValue++);
              }
              x[row][col] = encodings.get(fieldName)
                  .get(node.get(fieldName).asText());
              break;
            }
            case INTEGER:
            case DOUBLE:
            {
              x[row][col] = node.get(fieldName).asDouble();
            }
          }
          col++;
        }
        row++;
      }

      return x;
    }
  }
}
