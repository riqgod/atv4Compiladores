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
import br.ufpe.cin.if688.minijava.ast.Exp;
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
import br.ufpe.cin.if688.minijava.ast.Type;
import br.ufpe.cin.if688.minijava.ast.VarDecl;
import br.ufpe.cin.if688.minijava.ast.While;
import br.ufpe.cin.if688.minijava.symboltable.Class;
import br.ufpe.cin.if688.minijava.symboltable.Method;
import br.ufpe.cin.if688.minijava.symboltable.SymbolTable;
import br.ufpe.cin.if688.minijava.symboltable.Variable;
 
public class TypeCheckVisitor implements IVisitor<Type> {
 
    private SymbolTable symbolTable;
    private Class currClass;
    private Method currMethod;
    private boolean check;
   
    public TypeCheckVisitor(SymbolTable st) {
        this.check = false;
        symbolTable = st;
       
    }
   
    public Type visit(Program n) {
       
        n.m.accept(this);
        for (int i = 0; i < n.cl.size(); i++) {
            n.cl.elementAt(i).accept(this);
        }
       
        return null;
    }
 
    public Type visit(MainClass n) {
       
        this.currClass = this.symbolTable.getClass(n.i1.toString());
        this.currMethod = this.symbolTable.getMethod("main", currClass.getId());
       
        n.i1.accept(this);
        n.i2.accept(this);
        n.s.accept(this);
        currClass = null;
        currMethod = null;
        return null;
    }
 
    public Type visit(ClassDeclSimple n) {
       
        this.currClass = this.symbolTable.getClass(n.i.toString());
       
        n.i.accept(this);
        for (int i = 0; i < n.vl.size(); i++) {
            n.vl.elementAt(i).accept(this);
        }
        for (int i = 0; i < n.ml.size(); i++) {
            n.ml.elementAt(i).accept(this);
        }
       
        currClass = null;
        return null;
    }
 
   
    public Type visit(ClassDeclExtends n) {
       
        this.currClass = this.symbolTable.getClass(n.i.s);
        n.i.accept(this);
        n.j.accept(this);
        for (int i = 0; i < n.vl.size(); i++) {
            n.vl.elementAt(i).accept(this);
        }
        for (int i = 0; i < n.ml.size(); i++) {
            n.ml.elementAt(i).accept(this);
        }
       
        currClass = null;
        return null;
    }
 
    public Type visit(VarDecl n) {
       
       n.i.accept(this);
       Type retorno = n.t.accept(this);
        return retorno;
    }
 
    public Type visit(MethodDecl n) {
 
        currMethod = symbolTable.getMethod(n.i.toString(), currClass.getId());
       
        Type varDecl = n.e.accept(this);
       
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
        Type returnType = n.t.accept(this);
       this.check = symbolTable.compareTypes(returnType, varDecl);
        if(this.check) {
            this.check=false;
        }else {
           
            System.out.println("Erro Declaração de Método: " + currMethod.getId());            
            String type1 = stringTypeInstanceOf(varDecl);
            String type2 = stringTypeInstanceOf(returnType);
         
            System.out.println("Expectativa: " + type2 + "." + " Realidade: " + type1);
           
        }
        currMethod = null;
        return varDecl;
    }
 
    public Type visit(Formal n) {
        n.i.accept(this);
        Type type = n.t.accept(this);
        return type;
    }
 
    public Type visit(IntArrayType n) {
        return n;
    }
 
    public Type visit(BooleanType n) {
        return n;
    }
 
    public Type visit(IntegerType n) {
        return n;
    }
 
 
    public Type visit(IdentifierType n) {
        return n;
    }
 
    public Type visit(Block n) {
        for (int i = 0; i < n.sl.size(); i++) {
            n.sl.elementAt(i).accept(this);
        }
        return null;
    }
 
    public Type visit(If n) {
 
       n.s1.accept(this);
       n.s2.accept(this);
       Type expType = n.e.accept(this);
       Type check = new BooleanType();
       this.check = this.symbolTable.compareTypes(expType, check);
       if(this.check) {
           this.check = false;
       }else {
           
            System.out.println("Erro (if) - Expectativa: Boolean. Realidade: "+ stringTypeInstanceOf(expType));
           
            return null;
        }
   
        return null;
    }
 
    // Exp e;
    // Statement s;
    public Type visit(While n) {  
        Type expType = n.e.accept(this);
        Type check = new BooleanType();
        this.check = this.symbolTable.compareTypes(expType, check);
        if(this.check) {
           
        }else {
            System.out.println("Erro (while) - Expectativa: Boolean. Realidade: "+ stringTypeInstanceOf(expType));
           
            return null;
        }
        n.s.accept(this);
        return null;
    }
 
    // Exp e;
    public Type visit(Print n) {
        n.e.accept(this);
        return null;
    }
 
    // Identifier i;
    // Exp e;
    public Type visit(Assign n) {
    	
    	Type typeDecl = symbolTable.getVarType(currMethod, currClass, n.i.s);
        Type typeUsed = n.e.accept(this);
        //possível erro (riq)
        this.check = symbolTable.compareTypes(typeDecl, typeUsed);
        if(this.check) {
           
        }else {
            System.out.println("Erro (Assign) - Variável: " + n.i.s);
            System.out.println("Expectativa: " + stringTypeInstanceOf(typeDecl) + ". Realidade:"+ stringTypeInstanceOf(typeUsed));
           
        }
       
       
       n.i.accept(this);
       return null;
    }
 
    // Identifier i;
    // Exp e1,e2;
    public Type visit(ArrayAssign n) {
       
        Type index = n.e1.accept(this);
        this.check = index instanceof IntegerType;
        if(this.check) {
        	//tá de brinks, oq o sapo tá fazendo
        }else {
            System.out.println("(ArrayAssign) - Index não é Integer, ele é: "+stringTypeInstanceOf(index));
            return null;
        }
        Type varDeclType = symbolTable.getVarType(currMethod, currClass, n.i.s);
        Type varUsedType = n.e2.accept(this);
        this.check = (varDeclType instanceof IntArrayType && varUsedType instanceof IntegerType);
       if(this.check) {
    	  //Ahhhh, faz o que tu quiser mano!
       }else {
           System.out.println("Erro (ArrayAssign): " + n.i.toString());
           System.out.println("Expectativa: IntArrayType, Integer. Realidade: " + stringTypeInstanceOf(varDeclType) +
                    ", "+ stringTypeInstanceOf(varUsedType));
           return null;
           
       }
        return null;
    }
 
    // Exp e1,e2;
    public Type visit(And n) {
        //possivel erro
        Type typeLeft = n.e1.accept(this);
        Type typeRight = n.e2.accept(this);
        Type check = new BooleanType();
       
        if(!symbolTable.compareTypes(typeLeft, check)||(!symbolTable.compareTypes(typeRight, check))) {
           
            System.out.println("Erro (And) - expressões não são booleans");
            System.out.println("arg1: "+stringTypeInstanceOf(typeLeft)+" arg2: "+stringTypeInstanceOf(typeRight));
           
        }
       
        return check;
    }
 
    // Exp e1,e2;
    public Type visit(LessThan n) {
        Type typeLeft = n.e1.accept(this);
        Type typeRight = n.e2.accept(this);
        Type integer = new IntegerType();
       
        if(!this.symbolTable.compareTypes(typeLeft, integer)||(!symbolTable.compareTypes(typeRight, integer))) {
            System.out.println("Erro (LessThan) - expressões não são integer");
            System.out.println("arg1: "+stringTypeInstanceOf(typeLeft)+" arg2: "+stringTypeInstanceOf(typeRight));
           
        }
        return new BooleanType();
    }
 
    // Exp e1,e2;
    public Type visit(Plus n) {
        Type typeLeft = n.e1.accept(this);
        Type typeRight = n.e2.accept(this);
        Type integer = new IntegerType();
       
        if(!this.symbolTable.compareTypes(typeLeft, integer)||(!symbolTable.compareTypes(typeRight, integer))) {
            System.out.println("Erro (Plus) - argumentos não são integer");
            System.out.println("arg1: "+stringTypeInstanceOf(typeLeft)+" arg2: "+stringTypeInstanceOf(typeRight));
           
        }
       
        return integer;
    }
 
    // Exp e1,e2;
    public Type visit(Minus n) {
        Type typeLeft = n.e1.accept(this);
        Type typeRight = n.e2.accept(this);
        Type integer = new IntegerType();
       
        if(!this.symbolTable.compareTypes(typeLeft, integer)||(!symbolTable.compareTypes(typeRight, integer))) {
            System.out.println("Erro (Minus) - argumentos não são integer");
            System.out.println("arg1: "+stringTypeInstanceOf(typeLeft)+" arg2: "+stringTypeInstanceOf(typeRight));
           
        }
       
        return integer;
    }
 
    // Exp e1,e2;
    public Type visit(Times n) {
        Type typeLeft = n.e1.accept(this);
        Type typeRight = n.e2.accept(this);
        Type integer = new IntegerType();
       
        if(!this.symbolTable.compareTypes(typeLeft, integer)||(!symbolTable.compareTypes(typeRight, integer))) {
            System.out.println("Erro (Times) - argumentos não são integer");
            System.out.println("arg1: "+stringTypeInstanceOf(typeLeft)+" arg2: "+stringTypeInstanceOf(typeRight));
           
        }
       
        return integer;
    }
 
    // Exp e1,e2;
    public Type visit(ArrayLookup n) {
        Type t1 = n.e1.accept(this);
        Type t2 = n.e2.accept(this);
        Type arrayLookUp = new IntArrayType();
        Type integer = new IntegerType();
       
        if(!this.symbolTable.compareTypes(t1, arrayLookUp)||(!this.symbolTable.compareTypes(t2, integer))) {
            System.out.println("Erro (ArrayLookup) - argumentos errados. Esperado: arrayLookUp e integer");
            System.out.println("Exp1: "+stringTypeInstanceOf(t1)+" Exp2: "+stringTypeInstanceOf(t2));
           
        }
       
        return integer;
    }
 
    // Exp e;
    public Type visit(ArrayLength n) {
        Type t = n.e.accept(this);
        Type intArrayType = new IntArrayType();
        if(!symbolTable.compareTypes(t,intArrayType)) {
            System.out.println("Erro (ArrayLength) - argumento não é IntArrayType");
            System.out.println("arg: "+stringTypeInstanceOf(t));
           
        }
        return new IntegerType();
    }
 
    // Exp e;
    // Identifier i;
    // ExpList el;
    public Type visit(Call n) {
       
        Type returnType = null;
       
        Type exp = n.e.accept(this);
       
        if(n.e instanceof This) {
           
            if(this.currClass.getMethod(n.i.s)==null) {
            System.out.println("Erro (Call) - Tentando acessar método inexistente: "+n.i.s); 
           
            }else {
            returnType = this.currClass.getMethod(n.i.s).type();
            }
        }
        else if(exp instanceof IdentifierType) {
            Class cc = this.symbolTable.getClass(((IdentifierType) exp).s);
            Method cm = this.symbolTable.getMethod(n.i.s, cc.getId());
           
            Class currentClass = this.currClass;
            this.currClass = cc;
           
           
            int i;
            for ( i = 0; i < n.el.size(); i++) {
                Type pType = n.el.elementAt(i).accept(this);
           
                Variable pDecl = cm.getParamAt(i);
               
                if(pDecl == null) {
                    System.out.println("Erro (Call) - Parâmetros Diferentes");
                    System.out.println("Método: "+cm.getId());
                   
                    return null;
                }
                Type pDeclType = cm.getParamAt(i).type();
             
                if(!this.symbolTable.compareTypes(pType, pDeclType)) {
                    System.out.println("Erro (Call) - Expectativa: "+stringTypeInstanceOf(pDeclType) + ". Realidade: "+ stringTypeInstanceOf(pType));
                    return null;
                }
            }
            if(cm.getParamAt(i) == null) {
                Type idType = n.i.accept(this);
                currClass = currentClass;
                return idType;
               
            }else {
                System.out.println("Erro (Call) - Parâmetros Diferentes");
                System.out.println("Método: "+cm.getId());
               
                return null;
            }
           
        }
       
        return returnType;
    }
 
    // int i;
    public Type visit(IntegerLiteral n) {
        return new IntegerType();
    }
 
    public Type visit(True n) {
        return new BooleanType();
    }
 
    public Type visit(False n) {
        return new BooleanType();
    }
 
    // String s;
    public Type visit(IdentifierExp n) {
       
        return symbolTable.getVarType(this.currMethod, this.currClass, n.s);
    }
 
    public Type visit(This n) {
        return currClass.type();
    }
 
    // Exp e;
    public Type visit(NewArray n) {
        Type t = n.e.accept(this);
        Type integer = new IntegerType();
       
        if(!symbolTable.compareTypes(t, integer)) {
            System.out.println("Erro (NewArray) - Expectativa: Integer. Realidade: "+stringTypeInstanceOf(t));
           
        }
        return new IntArrayType();
    }
 
    // Iid.dentifier i;
    public Type visit(NewObject n) {
        return n.i.accept(this);
    }
 
    // Exp e;
    public Type visit(Not n) {
       Type not = n.e.accept(this);
       Type check = new BooleanType();
       if(!this.symbolTable.compareTypes(not, check)) {
           System.out.println("Erro (Not) - Esperado: Boolean. Recebido: "+stringTypeInstanceOf(not));
           
       }
       
       return check;
    }
 
    // String s;
    public Type visit(Identifier n) {
 
        if(currClass.containsVar(n.toString())) {
            return symbolTable.getVarType(currMethod, currClass, n.toString());
        }
        if(currClass.containsMethod(n.toString())) {
            return symbolTable.getMethodType(n.toString(), currClass.getId());
        }
        if(currMethod != null && currMethod.containsVar(n.toString())) {
            return currMethod.getVar(n.toString()).type();
        }
        if(currMethod != null && currMethod.containsParam(n.toString())) {
            return currMethod.getParam(n.toString()).type();
        } else {
            Class classe = this.symbolTable.getClass(n.toString());
            if(classe == null) {
                System.out.println("Erro (Identifier) - Variável não encontrada: " + n.s + " em nenhum método ou classe");
               
            } else {
                return classe.type();
            }
        }
		return null;
 
    }
   
   
    public String stringTypeInstanceOf(Type type) {
        if(type instanceof IntegerType) {
            return "Integer";
        }else if(type instanceof BooleanType) {
            return "Boolean";
        }else if(type instanceof IntArrayType) {
            return "IntArrayType";
        }else if(type instanceof IdentifierType) {
            return "Id";
        }  
        else {
            return "Nenhum tipo encontrado";
        }
    }
}