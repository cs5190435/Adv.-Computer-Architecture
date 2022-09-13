
public class Predictor2400 extends Predictor {

	Table gHRTable;
	Table pHRtable;
	//Register gHRegister;
	
	public Predictor2400() {
		this.pHRtable = new Table(1024,2);		//Pattern History Table.
		//this.gHRTable  = new Table();
		this.gHRTable =  new Table(64,4);    //4-bit Global History Table.
	}


	public void Train(long address, boolean outcome, boolean predict) {
		int PC = (1 << 6);
		PC -=1 ;
		PC = (int)(PC&address);

		int gind =  (1 << 6);
		gind -= 1;
		gind = (gind << 4);
		gind = (int)(gind&address);
		gind = (gind >> 4);
		//gind = gind%88;
		int GC = gHRTable.getInteger(gind, 0, 3);
		PC = PC << 4;
		//PC = (PC^GC);
		PC += GC;
		int pres = pHRtable.getInteger(PC, 0, 1);

		

		if(predict == outcome){
			if(predict == false){
				pHRtable.setInteger(PC,0, 1, 0);
				GC = (GC>>1);
				gHRTable.setInteger(gind, 0, 3, GC);
				
				
			}
			else{
				int req = (1 << 3);
				GC = (GC >> 1);
				GC += req;
				gHRTable.setInteger(gind, 0, 3, GC);
				pHRtable.setInteger(PC,0, 1, 3);
			}
		}

		else if(predict == false && outcome == true){
			
			GC = (GC >> 1);
			int req = (1<<3);
			GC += req;
			pHRtable.setInteger(PC,0, 1, pres+1);
			gHRTable.setInteger(gind, 0, 3, GC);
		}

		else if(predict == true && outcome == false){
			
			GC = GC >> 1;
			// int req = (1<<4);
			// GC += req;
			pHRtable.setInteger(PC, 0, 1, pres-1);
			gHRTable.setInteger(gind, 0, 3, GC);
		}

	}

	public boolean predict(long address){
		int PC = (1 << 6);
		PC -=1 ;
		PC = (int)(PC&address);

		int gind =  (1 << 6);
		gind -= 1;
		gind = (gind << 4);
		gind = (int)(gind&address);
		gind = (gind >> 4);
		//gind = gind%88;

		int GC = gHRTable.getInteger(gind, 0, 3);
		//PC = (PC << 2);
		PC = PC << 4;
		//PC = (PC^GC);
		PC = PC + GC;

		int entry = pHRtable.getInteger(PC, 0, 1);
		if(entry <= 1){
			return false;
		}
		else{
			return true;
		}
	}

}
