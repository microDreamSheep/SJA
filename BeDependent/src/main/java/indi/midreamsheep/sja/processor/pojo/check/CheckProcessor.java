package indi.midreamsheep.sja.processor.pojo.check;

import com.google.auto.service.AutoService;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Names;
import indi.midreamsheep.sja.annotation.pojo.check.CheckAll;
import indi.midreamsheep.sja.processor.pojo.check.entity.FiledExpression;
import sun.reflect.generics.tree.Tree;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AutoService(Processor.class)
public class CheckProcessor extends AbstractProcessor {

    private Messager messager;
    private Context context;
    private JavacTrees trees;
    private TreeMaker treeMaker;
    private Names names;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.messager = processingEnv.getMessager();
        this.context = ((JavacProcessingEnvironment) processingEnv).getContext();
        this.trees = JavacTrees.instance(processingEnv);
        this.treeMaker = TreeMaker.instance(context);
        this.names = new Names(context);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        //获取所有被Check注解的类
        //检查是否满足CheckerInter接口
        //获取所有字段
        //将字段进行封装
        //生成方法

        for (Element element : roundEnv.getElementsAnnotatedWith(CheckAll.class)) {
            if (CheckUtil.check(element)){
                messager.printMessage(Diagnostic.Kind.ERROR,"the class "+element.getClass().getName()+"is not implement the CheckInter truly or not a class");
                return true;
            }

            //生成方法
            JCTree.JCClassDecl tree = (JCTree.JCClassDecl) trees.getTree(element);;

            List<FiledExpression> filedExpressions = new LinkedList<>();

            tree.accept(new TreeTranslator() {
                @Override
                public void visitVarDef(JCTree.JCVariableDecl jcVariableDecl) {
                    super.visitVarDef(jcVariableDecl);
                    FiledExpression filedExpression = new FiledExpression();
                    filedExpression.setFieldCheck(jcVariableDecl);
                    filedExpressions.add(filedExpression);
                }
            });

            //生成方法,方法格式如下
            //名字为check 返回值为boolean
            //方法体为 返回filedExpressions里所有表达式的&&
            //方法参数为所有空

            //生成方法体
            StringBuilder checkExpression = new StringBuilder();
            filedExpressions.forEach(filedExpression -> {
                checkExpression.append(filedExpression.getFieldCheck()).append("&&");
            });
            checkExpression.delete(checkExpression.length()-2,checkExpression.length()-1);
            JCTree.JCExpression jcExpression
                    = treeMaker.Binary(JCTree.Tag.AND,treeMaker.Ident(names.fromString(checkExpression.toString())),treeMaker.Literal(true));


            List<JCTree.JCStatement> objects = new LinkedList<>();
            objects.add(treeMaker.Return(jcExpression));

        }
        return true;
    }

    /**
     *
     * */
    private JCTree.JCMethodDecl generateGetterMethod(String methodBody) {
        //TODO
        return null;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> sets = new HashSet<>();
        sets.add(CheckAll.class.getName());
        return sets;
    }

    @Override
    public Iterable<? extends Completion> getCompletions(Element element, AnnotationMirror annotation, ExecutableElement member, String userText) {
        return super.getCompletions(element, annotation, member, userText);
    }

    //设置版本
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}

