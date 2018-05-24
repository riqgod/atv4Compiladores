package Antlr;

import java.util.Iterator;

import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import Antlr.AntlrParser.ClassDeclarationContext;
import Antlr.AntlrParser.ExpressionContext;
import Antlr.AntlrParser.GoalContext;
import Antlr.AntlrParser.IdentifierContext;
import Antlr.AntlrParser.IntegerContext;
import Antlr.AntlrParser.MainClassContext;
import Antlr.AntlrParser.MethodDeclarationContext;
import Antlr.AntlrParser.StatementContext;
import Antlr.AntlrParser.TypeContext;
import Antlr.AntlrParser.VarDeclarationContext;
import br.ufpe.cin.if688.minijava.ast.*;

public class VisitorInterface implements AntlrVisitor<Object> {

	@Override
	public Object visit(ParseTree x) {
		return x.accept(this);
	}

	@Override
	public Object visitChildren(RuleNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitErrorNode(ErrorNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitTerminal(TerminalNode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitIdentifier(IdentifierContext ctx) {
		Identifier idf = (Identifier) ctx.IDENTIFIER().accept(this);
		return idf;
	}

	@Override
	public Object visitMethodDeclaration(MethodDeclarationContext ctx) {

		Type tipo = (Type) ctx.type(0).accept(this);
		Identifier idf = (Identifier) ctx.identifier(0).accept(this);
		FormalList fl = new FormalList();
		Iterator<TypeContext> itt = (Iterator<TypeContext>) ctx.type().iterator();
		Iterator<IdentifierContext> itd = (Iterator<IdentifierContext>) ctx.identifier().iterator();
		itt.next();
		itd.next();

		while (itt.hasNext() & itd.hasNext()) {
			Formal frm = new Formal((Type) itt.next().accept(this), (Identifier) itd.next().accept(this));
			fl.addElement(frm);
		}

		VarDeclList vdl = new VarDeclList();
		Iterator<VarDeclarationContext> itv = (Iterator<VarDeclarationContext>) ctx.varDeclaration().iterator();
		while (itv.hasNext()) {
			vdl.addElement((VarDecl) itv.next().accept(this));
		}

		StatementList stml = new StatementList();
		Iterator<StatementContext> its = (Iterator<StatementContext>) ctx.statement().iterator();
		while (its.hasNext()) {
			Statement s = (Statement) its.next().accept(this);
			stml.addElement(s);
		}

		Exp e = (Exp) ctx.expression().accept(this);

		return new MethodDecl(tipo, idf, fl, vdl, stml, e);
	}

	@Override
	public Object visitGoal(GoalContext ctx) {

		MainClass mc = (MainClass) ctx.mainClass().accept(this);
		ClassDeclList cdl = new ClassDeclList();
		Iterator<ClassDeclarationContext> itc = (Iterator<ClassDeclarationContext>) ctx.classDeclaration().iterator();
		while (itc.hasNext()) {
			cdl.addElement((ClassDecl) itc.next().accept(this));
		}

		return new Program(mc, cdl);
	}

	@Override
	public Object visitExpression(ExpressionContext ctx) {

		int numExp = ctx.expression().size();
		int numChild = ctx.getChildCount();

		if (numChild == 1) {

			String aux = ctx.getChild(0).getText();
			if (aux == "true") {
				return new True();
			} else if (aux == "false") {
				return new False();
			} else if (aux == "this") {
				return new This();
			} else if (aux.matches("\\d+")) {
				Integer i = (Integer) ctx.integer().accept(this);
				return new IntegerLiteral(i);
			} else {
				IdentifierExp idf = (IdentifierExp) ctx.identifier().accept(this);
				return idf;
				// return new IdentifierExp(aux); //https://pastebin.com/PjDkSQ5f
			}

		} else if (numExp == 2) {

			Exp exp1 = (Exp) ctx.expression(0).accept(this);
			Exp exp2 = (Exp) ctx.expression(1).accept(this);
			String op = ctx.getChild(1).getText();

			if (op == "&&") {
				return new And(exp1, exp2);
			} else if (op == "<") {
				return new LessThan(exp1, exp2);
			} else if (op == "+") {
				return new Plus(exp1, exp2);
			} else if (op == "-") {
				return new Minus(exp1, exp2);
			} else if (op == "*") {
				return new Times(exp1, exp2);
			} else { // if (op == "[") {
				return new ArrayLookup(exp1, exp2);
			}

		} else if (numExp == 1) {

			String aux = ctx.getChild(0).getText();
			Exp exp = (Exp) ctx.expression(0).accept(this);

			if (aux == "!") {
				return new Not(exp);
			} else if (aux == "(") {
				return exp;
			} else if (aux == ".") {
				return new ArrayLength(exp);
			} else {
				return new NewArray(exp);
			}
		} else if (numChild >= 5) {

			Exp e1 = (Exp) ctx.expression(0).accept(this);
			Identifier idf = (Identifier) ctx.identifier().accept(this);
			ExpList el = new ExpList();
			Iterator<ExpressionContext> ite = (Iterator<ExpressionContext>) ctx.expression().iterator();
			ite.next();
			if (ite.hasNext()) {
				el.addElement((Exp) ite.next().accept(this));
				while (ite.hasNext()) {
					el.addElement((Exp) ite.next().accept(this));
				}
			}
			return new Call(e1, idf, el);
		} else {
			Identifier idf = (Identifier) ctx.identifier().accept(this);
			return new NewObject(idf);
		}
	}

	@Override
	public Object visitMainClass(MainClassContext ctx) {

		Identifier idf = (Identifier) ctx.identifier(0).accept(this);
		Identifier idf2 = (Identifier) ctx.identifier(1).accept(this);
		Statement s = (Statement) ctx.statement().accept(this);

		return new MainClass(idf, idf2, s);
	}

	@Override
	public Object visitStatement(StatementContext ctx) {

		int numExp = ctx.expression().size();
		String aux = ctx.getChild(0).getText();

		if (numExp == 1) {
			Exp exp = (Exp) ctx.expression(0).accept(this);

			if (aux == "if") {
				Statement s1 = (Statement) ctx.statement(0).accept(this);
				Statement s2 = (Statement) ctx.statement(1).accept(this);
				return new If(exp, s1, s2);
			} else if (aux == "while") {
				Statement s1 = (Statement) ctx.statement(0).accept(this);
				return new While(exp, s1);
			} else if (aux == "System.out.println") {
				return new Print(exp);
			} else {
				Identifier idf = (Identifier) ctx.identifier().accept(this);
				return new Assign(idf, exp);
			}
		} else if (numExp == 2) {
			Identifier idf = (Identifier) ctx.identifier().accept(this);
			Exp exp1 = (Exp) ctx.expression(0).accept(this);
			Exp exp2 = (Exp) ctx.expression(1).accept(this);
			return new ArrayAssign(idf, exp1, exp2);
		} else {
			// "{" ( Statement )* "}"
			StatementList sl = new StatementList();
			Iterator<StatementContext> its = (Iterator<StatementContext>) ctx.statement().iterator();
			while (its.hasNext()) {
				sl.addElement((Statement) its.next().accept(this));
			}
			return new Block(sl);
		}
	}

	@Override
	public Object visitInteger(IntegerContext ctx) {
		Integer i = (Integer) ctx.INTEGER().accept(this);
		return new IntegerLiteral(i);
	}

	@Override
	public Object visitType(TypeContext ctx) {
		int numChild = ctx.getChildCount();
		String aux = ctx.getChild(0).getText();
		if (numChild == 3) {
			return new IntArrayType();
		} else if (aux == "int") {
			return new IntegerType();
		} else if (aux == "boolean") {
			return new BooleanType();
		} else {
			Identifier idf = (Identifier) ctx.identifier().accept(this);
			return idf;
		}
	}

	@Override
	public Object visitVarDeclaration(VarDeclarationContext ctx) {

		Type tipo = (Type) ctx.type().accept(this);
		Identifier idf = (Identifier) ctx.identifier().accept(this);

		return new VarDecl(tipo, idf);
	}

	@Override
	public Object visitClassDeclaration(ClassDeclarationContext ctx) {

		Identifier idf = (Identifier) ctx.identifier(0).accept(this);

		VarDeclList vdl = new VarDeclList();
		Iterator<VarDeclarationContext> itv = (Iterator<VarDeclarationContext>) ctx.varDeclaration().iterator();
		while (itv.hasNext()) {
			vdl.addElement((VarDecl) itv.next().accept(this));
		}
		MethodDeclList mdl = new MethodDeclList();
		Iterator<MethodDeclarationContext> itm = (Iterator<MethodDeclarationContext>) ctx.methodDeclaration()
				.iterator();
		while (itm.hasNext()) {
			mdl.addElement((MethodDecl) itm.next().accept(this));
		}

		Iterator<IdentifierContext> itd = (Iterator<IdentifierContext>) ctx.identifier().iterator();
		itd.next();
		if (itd.hasNext()) {
			Identifier idf2 = (Identifier) itd.next().accept(this);
			return new ClassDeclExtends(idf, idf2, vdl, mdl);
		} else {
			return new ClassDeclSimple(idf, vdl, mdl);
		}
	}

}
