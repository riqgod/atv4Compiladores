package br.ufpe.cin.if688.minijava.visitor;
 
import br.ufpe.cin.if688.minijava.ast.And;
import br.ufpe.cin.if688.minijava.ast.ArrayAssign;
import br.ufpe.cin.if688.minijava.ast.ArrayLength;
import br.ufpe.cin.if688.minijava.ast.ArrayLookup;
import br.ufpe.cin.if688.minijava.ast.Assign;
import br.ufpe.cin.if688.minijava.ast.Block;
import br.ufpe.cin.if688.minijava.ast.BooleanType;
import br.ufpe.cin.if688.minijava.ast.Call;
import br.ufpe.cin.if688.minijava.ast.ClassDeclExtends;
import br.ufpe.cin.if688.minijava.ast.ClassDeclSimple;
import br.ufpe.cin.if688.minijava.ast.False;
import br.ufpe.cin.if688.minijava.ast.Formal;
import br.ufpe.cin.if688.minijava.ast.Identifier;
import br.ufpe.cin.if688.minijava.ast.IdentifierExp;
import br.ufpe.cin.if688.minijava.ast.IdentifierType;
import br.ufpe.cin.if688.minijava.ast.If;
import br.ufpe.cin.if688.minijava.ast.IntArrayType;
import br.ufpe.cin.if688.minijava.ast.IntegerLiteral;
import br.ufpe.cin.if688.minijava.ast.IntegerType;
import br.ufpe.cin.if688.minijava.ast.LessThan;
import br.ufpe.cin.if688.minijava.ast.MainClass;
import br.ufpe.cin.if688.minijava.ast.MethodDecl;
import br.ufpe.cin.if688.minijava.ast.Minus;
import br.ufpe.cin.if688.minijava.ast.NewArray;
import br.ufpe.cin.if688.minijava.ast.NewObject;
import br.ufpe.cin.if688.minijava.ast.Not;
import br.ufpe.cin.if688.minijava.ast.Plus;
import br.ufpe.cin.if688.minijava.ast.Print;
import br.ufpe.cin.if688.minijava.ast.Program;
import br.ufpe.cin.if688.minijava.ast.This;
import br.ufpe.cin.if688.minijava.ast.Times;
import br.ufpe.cin.if688.minijava.ast.True;
import br.ufpe.cin.if688.minijava.ast.VarDecl;
import br.ufpe.cin.if688.minijava.ast.While;
import br.ufpe.cin.if688.minijava.symboltable.Class;
import br.ufpe.cin.if688.minijava.symboltable.Method;
import br.ufpe.cin.if688.minijava.symboltable.SymbolTable;
 
public class BuildSymbolTableVisitor implements IVisitor<Void> {
 
    SymbolTable symbolTable;
    boolean check;
 
    public BuildSymbolTableVisitor() {
        symbolTable = new SymbolTable();
    }
 
    public SymbolTable getSymbolTable() {
        return symbolTable;
    }
 
 
    private Class currClass;
    private Method currMethod;
 
   
    public Void visit(Program n) {
 
        n.m.accept(this);
 
        for (int i = 0; i < n.cl.size(); i++) {
            n.cl.elementAt(i).accept(this);
        }
        return null;
    }
 
 
    public Void visit(MainClass n) {
       
     
       
       
        this.symbolTable.addClass(n.i1.s, null);
        this.currClass = this.symbolTable.getClass(n.i1.s);
        this.currClass.addMethod("main", null );
        this.currMethod = currClass.getMethod("main");
        this.currMethod.addParam(n.i2.s, new IntArrayType());
       
        n.i1.accept(this);
        n.i2.accept(this);
        n.s.accept(this);
       
       //reset
        this.currClass = null;
        this.currMethod = null;
        return null;
    }
 
   
    public Void visit(ClassDeclSimple n) {
       
               
        if(this.currClass == null) {
            this.check = this.symbolTable.addClass(n.i.s, null);
            if(this.check){
                //faz nada
            }else {
                System.out.println("Existe uma classe com este nome");
                return null;
               
            }
           
        }else {
            this.check = this.symbolTable.addClass(n.i.s, this.currClass.getId());
            if(this.check){
                //faz nada
            }else {
                System.out.println("Existe uma classe com este nome");
                return null;
            }
           
        }
   
        this.currClass = this.symbolTable.getClass(n.i.s);
        n.i.accept(this);
       
        for (int i = 0; i < n.vl.size(); i++) {
            n.vl.elementAt(i).accept(this);
        }
        for (int i = 0; i < n.ml.size(); i++) {
            n.ml.elementAt(i).accept(this);
        }
       
        this.currClass = null;
        return null;
    }
 
 
    public Void visit(ClassDeclExtends n) {
 
       
        if(this.currClass == null) {
            this.check = this.symbolTable.addClass(n.i.s, null);
            if(this.check){
                // faz nada
            }else {
                System.out.println("Existe uma classe com este nome");
                return null;
            }
        }else {
            this.check = this.symbolTable.addClass(n.i.s, this.currClass.getId());
            if(this.check){
                //faz nada
            }else {
                System.out.println("Existe uma classe com este nome");
                return null;
            }
        }
       
        this.currClass = this.symbolTable.getClass(n.i.s);              
        n.i.accept(this);
        n.j.accept(this);
        for (int i = 0; i < n.vl.size(); i++) {
            n.vl.elementAt(i).accept(this);
        }
        for (int i = 0; i < n.ml.size(); i++) {
            n.ml.elementAt(i).accept(this);
        }
       
        this.currClass = null;
        return null;
    }
 
 
   
    public Void visit(VarDecl n) {
       
        if(this.currMethod==null) {
            this.check = this.currClass.addVar(n.i.s,n.t);
            if(this.check) {
                // faz nada
            }else {
                 System.out.println("Variável já foi declarada na classe");
                 return null;
            }
        }else {
            this.check = this.currMethod.addVar(n.i.s,n.t);
            if(this.check) {
               
            }else {
                 System.out.println("Variável já foi declarada no método");
                 return null;
            }
        }
       
        n.t.accept(this);
        n.i.accept(this);
        return null;
    }
 
   
    public Void visit(MethodDecl n) {
       
       
        if(this.currClass == null) {
             System.out.println("Método declarado fora de classe");
             return null;
        }else {
            this.check = this.currClass.addMethod(n.i.s, n.t);
             if(this.check) {
                 //segue o jogo
             }else {
                 System.out.println("Método já foi criado nesta clase");
                 return null;
             }
        }
       
       
       
        this.currMethod = this.currClass.getMethod(n.i.toString());
       
        n.t.accept(this);
        n.i.accept(this);
       
        for (int i = 0; i < n.fl.size(); i++) {
            n.fl.elementAt(i).accept(this);
        }
        for (int i = 0; i < n.vl.size(); i++) {
            n.vl.elementAt(i).accept(this);
        }
        for (int i = 0; i < n.sl.size(); i++) {
            n.sl.elementAt(i).accept(this);
        }
       
        n.e.accept(this);
        currMethod = null;
        return null;
    }
 
   
    public Void visit(Formal n) {
       
       
        if(this.currMethod==null) {
            System.out.println("sem método");
            return null;
        }else {
            this.check = this.currMethod.addParam(n.i.toString(), n.t);
            if(this.check) {
                //faz nada
            }else{
                System.out.println("Parametro com mesmo nome!");
            }
        }
           
       
        n.t.accept(this);
        n.i.accept(this);
        return null;
    }
 
    public Void visit(IntArrayType n) {
        return null;
    }
 
    public Void visit(BooleanType n) {
        return null;
    }
 
    public Void visit(IntegerType n) {
        return null;
    }
 
    public Void visit(IdentifierType n) {
        return null;
    }
 
 
    public Void visit(Block n) {
        for (int i = 0; i < n.sl.size(); i++) {
            n.sl.elementAt(i).accept(this);
        }
        return null;
    }
 
    public Void visit(If n) {
        n.e.accept(this);
        n.s1.accept(this);
        n.s2.accept(this);
        return null;
    }
 
 
    public Void visit(While n) {
        n.e.accept(this);
        n.s.accept(this);
        return null;
    }
 
    public Void visit(Print n) {  
        n.e.accept(this);
        return null;
    }
 
    public Void visit(Assign n) {
        n.i.accept(this);
        n.e.accept(this);
        return null;
    }
 
    public Void visit(ArrayAssign n) {
        n.i.accept(this);
        n.e1.accept(this);
        n.e2.accept(this);
        return null;
    }
 
    public Void visit(And n) {
        n.e1.accept(this);
        n.e2.accept(this);
        return null;
    }
 
    public Void visit(LessThan n) {
        n.e1.accept(this);
        n.e2.accept(this);
        return null;
    }
 
    public Void visit(Plus n) {
        n.e1.accept(this);
        n.e2.accept(this);
        return null;
    }
 
    public Void visit(Minus n) {
        n.e1.accept(this);
        n.e2.accept(this);
        return null;
    }
 
    public Void visit(Times n) {
        n.e1.accept(this);
        n.e2.accept(this);
        return null;
    }
 
    public Void visit(ArrayLookup n) {
        n.e1.accept(this);
        n.e2.accept(this);
        return null;
    }
 
    public Void visit(ArrayLength n) {
        n.e.accept(this);
        return null;
    }
 
 
    public Void visit(Call n) {
        n.e.accept(this);
        n.i.accept(this);
        for (int i = 0; i < n.el.size(); i++) {
            n.el.elementAt(i).accept(this);
        }
        return null;
    }
 
    public Void visit(IntegerLiteral n) {
        return null;
    }
 
    public Void visit(True n) {
        return null;
    }
 
    public Void visit(False n) {
        return null;
    }
 
 
    public Void visit(IdentifierExp n) {
        return null;
    }
 
    public Void visit(This n) {
        return null;
    }
 
 
    public Void visit(NewArray n) {
        n.e.accept(this);
        return null;
    }
 
    public Void visit(NewObject n) {
        return null;
    }
 
    public Void visit(Not n) {
        n.e.accept(this);
        return null;
    }
 
    public Void visit(Identifier n) {
        return null;
    }
}