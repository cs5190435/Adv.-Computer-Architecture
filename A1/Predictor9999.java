
public class Predictor9999 extends Predictor {

	Table gHRTable;
	Table pHRtable;			//predcitor 2.
	Table choice;			//choice table.
	Table bi_mode;
	
	public Predictor9999() {
		this.pHRtable = new Table(2048,2);		// Pattern history Table. 
		this.gHRTable =  new Table(256,4); 		// Global history register table.
		this.choice = new Table(1024, 2);   // Choice array for selecting and training both the predictors.
		this.bi_mode = new Table(2048, 1);   // g-share predictor
	}


	public void Train(long address, boolean outcome, boolean predict) {

		int PC = (1 << 7);
		PC -= 1;
		PC = (int)(PC&address);
		int gind = (1 << 8);
		gind -= 1;
		gind = (gind << 5);
		gind = (int)(gind&address);
		gind = (gind >> 5);

		int GC = gHRTable.getInteger(gind, 0, 3);

		PC = (PC << 4);
		//PC = (PC^GC);
		PC += GC;
		
		int us = 1<< 10;
		us -= 1;
		us = (int)(us&address);
		int PC_b = (1 << 11);
		PC_b -= 1;
		PC_b = (int)(PC_b&address);
		PC_b = (PC_b^GC);


		int P_res = pHRtable.getInteger(PC, 0, 1);  //result from predictor 2
		int B_res = bi_mode.getInteger(PC_b, 0, 0);  // result from predictor 1
		if(predict == outcome){
			if(predict == true){
				if(B_res == 1 && P_res > 1){
					//bi_mode.setInteger(PC_b, 0, 1, 3);
					pHRtable.setInteger(PC, 0, 1, 3);
					GC = (GC >> 1);
					//int req = (1<< 3);
					GC += (1 << 3);
					gHRTable.setInteger(gind, 0, 3, GC);
				}
				else if(B_res == 1 && P_res <= 1){
					//bi_mode.setInteger(PC_b, 0, 1,3);
					int temp = choice.getInteger(us, 0, 1);
					if(temp < 3){
						temp += 1;
						choice.setInteger(us, 0, 1,temp );

					}
					//P_res += 1;
					pHRtable.setInteger(PC, 0, 1, P_res+1);
					GC = (GC >> 1);
					GC += (1 << 3);
					gHRTable.setInteger(gind, 0, 3, GC);
				}
				else if(B_res == 0 && P_res > 1){
					int temp = choice.getInteger(us, 0, 1);
					if(temp > 0){
						temp -= 1;
						choice.setInteger(us, 0, 1,temp);
					}
					//B_res += 1;
					bi_mode.setInteger(PC_b, 0, 0, 1);
					pHRtable.setInteger(PC, 0, 1,3);
					GC = (GC >> 1);
					GC += (1 << 3);
					gHRTable.setInteger(gind, 0, 3, GC);
				}

			}

			else if(predict == false){
				if(B_res == 0 && P_res <= 1){
					//bi_mode.setInteger(PC_b, 0, 1, 0);
					pHRtable.setInteger(PC, 0, 1, 0);
					GC = (GC >> 1);
					//int req = (1<< 3);
					//GC += (1 << 3);
					gHRTable.setInteger(gind, 0, 3, GC);
				}
				else if(B_res == 0 && P_res > 1){
					//bi_mode.setInteger(PC_b, 0, 1,0);
					int temp = choice.getInteger(us, 0, 1);
					if(temp < 3){
						temp += 1;
						choice.setInteger(us, 0, 1,temp );
					}
					//P_res -= 1;
					pHRtable.setInteger(PC, 0, 1, P_res-1);
					GC = (GC >> 1);
					//GC += (1 << 3);
					gHRTable.setInteger(gind, 0, 3, GC);
				}
				else if(B_res == 1 && P_res <= 1){
					int temp = choice.getInteger(us, 0, 1);
					if(temp > 0){
						temp -= 1;
						choice.setInteger(us, 0, 1,temp);
					}
					//B_res -= 1;
					bi_mode.setInteger(PC_b, 0, 0, 0);
					pHRtable.setInteger(PC, 0, 1,0);
					GC = (GC >> 1);
					//GC += (1 << 3);
					gHRTable.setInteger(gind, 0, 3, GC);
				}
				// else if(B_res > 1 && P_res > 1){
				// 	bi_mode.setInteger(PC_b, 0, 0, 0);
				// 	pHRtable.setInteger(PC, 0, 1,P_res-1);
				// 	GC = (GC >> 1);
				// 	//GC += (1 << 3);
				// 	gHRTable.setInteger(gind, 0, 3, GC);
				// }

			}
			
		}

		else if(predict == true && outcome == false){
			int ch = choice.getInteger(us, 0, 1);
			if(P_res > 1 && B_res == 0){
				if(ch < 3){
					ch += 1;
				}
				choice.setInteger(us, 0, 1, ch);
				//bi_mode.setInteger(PC_b, 0, 1, 0);
				GC = (GC >> 1);
				gHRTable.setInteger(gind, 0, 3, GC);
				pHRtable.setInteger(PC, 0, 1, P_res-1);
			}
			else if(P_res <= 1 && B_res == 1){
				if(ch > 0){
					ch -= 1;
				}
				choice.setInteger(us, 0, 1, ch);
				GC = (GC >> 1);
				gHRTable.setInteger(gind, 0, 3, GC);
				bi_mode.setInteger(PC_b, 0, 0, B_res-1);
				pHRtable.setInteger(PC, 0, 1, 0);
			}

			else if(P_res > 1 && B_res == 1){

				bi_mode.setInteger(PC_b, 0, 0, B_res-1);
				pHRtable.setInteger(PC, 0, 1, P_res-1);
				GC = (GC >> 1);
				gHRTable.setInteger(gind, 0, 3, GC);
			}
		}

		else if(predict == false && outcome == true){
			int ch = choice.getInteger(us, 0, 1);
			if(P_res <= 1 && B_res == 1){
				if(ch < 3){
					ch += 1;
					choice.setInteger(us, 0, 1, ch);
				}
				
				//bi_mode.setInteger(PC_b, 0, 0, 3);

				GC = (GC >> 1);
				GC += (1 << 3);
				gHRTable.setInteger(gind, 0, 3, GC);
				pHRtable.setInteger(PC, 0, 1, P_res+1);
			}
			else if(P_res > 1 && B_res == 0){
				if(ch > 0){
					ch -= 1;
					choice.setInteger(us, 0, 1, ch);
				}
				
				GC = (GC >> 1);
				GC += (1 << 3);
				gHRTable.setInteger(gind, 0,3,GC);
				bi_mode.setInteger(PC_b, 0, 0, B_res+1);
				pHRtable.setInteger(PC,0, 1, 3);
			}

			else if(P_res <= 1 && B_res == 0){

				bi_mode.setInteger(PC_b, 0, 0, B_res+1);
				pHRtable.setInteger(PC, 0, 1,P_res+1);
				GC = (GC >> 1);
				GC += (1 << 3);
				gHRTable.setInteger(gind, 0, 3, GC);
			}

		}



	}


	public boolean predict(long address){

		int PC = (1 << 7);
		PC -= 1;
		PC = (int)(PC&address);
		int gind = (1 << 8);
		gind -= 1;
		gind = (gind << 5);
		gind = (int)(gind&address);
		gind = (gind >> 5);

		int GC = gHRTable.getInteger(gind, 0, 3);

		PC = (PC << 4);
		PC += GC;
		//PC = (PC^GC);
		int P_res = pHRtable.getInteger(PC, 0, 1);
		int PC_b = (1 << 11);
		PC_b -= 1;
		PC_b = (int)(PC_b&address);
		int us = 1<< 10;
		us -= 1;
		us = (int)(us&address);
		int ch = choice.getInteger(us, 0, 1);
		PC_b = (PC_b^GC);
		int B_res = bi_mode.getInteger(PC_b, 0, 0);
		
		if(ch <= 1){
			if(P_res > 1){return true;}
			else{return false;}
		}
		else{
			if(B_res == 0){return false;}
			else{return true;}
		}

	}

}