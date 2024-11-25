package Frontend.Syntax.Children;

import SymbolTable.VarSymbol.VarTypes;

import java.util.ArrayList;

import Frontend.Syntax.Syntax;

public class FuncFParams {
    static ArrayList<VarTypes> FuncFParamsAnalysis() {
        ArrayList<VarTypes> funcParams = new ArrayList<>();
        VarTypes vt;

        vt = FuncFParam.FuncFParamAnalysis();
        funcParams.add(vt);
        while (Tools.LookNextTK().tk.equals("COMMA")) {
            CompUnit.count++; // ,
            vt = FuncFParam.FuncFParamAnalysis();
            funcParams.add(vt);
        }

        Tools.WriteLine(Syntax.NodeType.FuncFParams, Tools.GetNowTK().id);

        return funcParams;
    }
}
