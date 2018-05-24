
class Teste{
	public static void main(String [ ] args){
		int i;
		i=9999;
		boolean b;
		b=true;
		while (b){				
			if (!b) {
			i=0;
			b=false;
			}else{
			i=1;
			b=false;
			}	
		}
		System.out.println(i);
	}
}
