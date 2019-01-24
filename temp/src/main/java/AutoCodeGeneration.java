

import java.io.*;
import java.text.MessageFormat;
import java.util.*;

public class AutoCodeGeneration {
  public static void main(String[] args) throws Exception {
    //代码生成的结果放到Dag.txt
    File file = new File(AutoCodeGeneration.class.getClassLoader().getResource("").getPath() + "/Dag.txt");
    if (file == null) {
      file.createNewFile();
    }
    OutputStream out = new FileOutputStream(file);
    System.setOut(new PrintStream(out));
    //读取result.txt文件中的影响关系
    Map<String, Set<String>> relations = new HashMap<String, Set<String>>();
    File resultFile = new File(AutoCodeGeneration.class.getClassLoader().getResource("").getPath() + "/pocTxt.txt");
    if (resultFile == null) {
      throw new Exception("文件不存在");
    }
    BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(resultFile)));
    String line = "";
    while (line != null) {
      line = reader.readLine();
      addInfluenceRelation(line, relations);
    }
    generateDAGCode(relations);
  }

  /**
   * 根据line的内容提取出对象间的影响关系
   * @param line
   * @param relations key：表或存储过程 value：影响的表或存储过程的集合
   */
  private static void addInfluenceRelation(String line, Map<String, Set<String>> relations) {
    if (line == null || !line.contains("->")) {
      return;
    }
    String[] relationObjs = line.split("->");
    String srcObj = format(relationObjs[0]);
    Set<String> influencesObjs = relations.get(srcObj);
    if (influencesObjs == null) {
      influencesObjs = new HashSet();
      relations.put(srcObj, influencesObjs);
    }
    influencesObjs.add(format(relationObjs[1]));
  }

  private static String format(String strWithsquareBrackets) {
    int leftSquareBracket = strWithsquareBrackets.trim().indexOf("[");
    if (leftSquareBracket <= 0) {
      return strWithsquareBrackets.trim();
    } else {
      return strWithsquareBrackets.trim().substring(0, leftSquareBracket);
    }
  }

  private static void generateDAGCode(Map<String, Set<String>> relations) {
    Set<String> allProNames = new HashSet<String>();
    Map<String, Set<String>> proRelations = new HashMap<String, Set<String>>();
    for (String srcObj : relations.keySet()) {
      if (!isProcedure(srcObj)) {
        //如果是表影响其他存储结构或表的形式：table -> {}，则不处理。
        continue;
      }
      String srcProcedureName = getProcedureName(srcObj);
      //srcObj 影响的所有存储过程的集合
      Set<String> influencesPros = new HashSet<String>();
      findInfluencesPros(influencesPros, relations, srcObj);

      allProNames.add(srcProcedureName);
      allProNames.addAll(influencesPros);
      proRelations.put(srcProcedureName, influencesPros);
    }
    generateTasksDefCode(allProNames);
    generateTasksRelationCode(proRelations);
  }

  private static void generateTasksRelationCode(Map<String,Set<String>> proRelations) {
    for(String pro : proRelations.keySet()){
      Set<String> influencesPros = proRelations.get(pro);
      if(influencesPros.contains(pro)){
        influencesPros.remove(pro);
      }
      //存在循环依赖则去除其中一个依赖关系
      for(String influencesPro : influencesPros){
        if(proRelations.get(influencesPro) != null && proRelations.get(influencesPro).contains(pro)){
          proRelations.get(influencesPro).remove(pro);
        }
      }
      //打印依赖关系
      if(influencesPros.size() <=0) {
        continue;
      } else if(influencesPros.size() == 1) {
        String influencesPro = new ArrayList<String>(influencesPros).get(0);
        System.out.println(pro + " >> " + influencesPro);
      } else if (influencesPros.size() > 1) {
        System.out.println(pro + " >> " + influencesPros.toString());
      }
    }
  }

  private static void generateTasksDefCode(Set<String> allProNames) {
    System.out.println();
    for (String p : allProNames) {
      System.out.println(MessageFormat.format("{0} = PythonOperator(\n"
          + "    task_id=''{1}'',\n"
          + "    python_callable=print_task_msg,\n"
          + "    dag=dag)\n", p, p));
    }
  }

  /**
   * 查找srcObj影响的所有存储过程，并将查找结果添加到influencesPros。
   * @param influencesPros
   * @param relations  所有的表/存储过程间的依赖关系
   * @param srcObj 表或存储过程
   */
  private static void findInfluencesPros(Set<String> influencesPros, Map<String, Set<String>> relations,
      String srcObj) {
    Set<String> influencesObjs = relations.get(srcObj);
    if (influencesObjs == null) {
      return;
    }
    for (String influencesObj : influencesObjs) {
      if (isProcedure(influencesObj)) {
        influencesPros.add(getProcedureName(influencesObj));
      } else {
        //如果是influencesObj是表，则继续查找该表影响的所有存储过程
        findInfluencesPros(influencesPros, relations, influencesObj);
      }
    }
  }

  private static String getProcedureName(String procedureStr) {
    int index = procedureStr.indexOf("：");
    if (index >= 0) {
      return procedureStr.substring(index + 1, procedureStr.length() - 1).trim().replace(".","_");
    }
    return procedureStr;
  }

  private static boolean isProcedure(String str) {
    if (str.contains("存储过程")) {
      return true;
    } else {
      return false;
    }
  }

}
