(ns navalgrid.persistence.regions)

(def all
  [{:label "NÖRDLICHES EISMEER"
    :name  "Arctic Ocean"
    :ids   ["ÄJ" "ÄH" "ÄG" "ÄF" "AA" "AB" "AC" "AT" "AS" "AW" "AU" "AR" "XL" "XM" "XN" "XA" "XC" "XE" "XJ" "XK" "XG" "XH" "XO"
            "XP" "XQ" "XR" "XZ" "XB" "XD" "XF" "YD" "YE" "YB" "YC"]}
   {:label "EUROPÄISCHES NORDMEER"
    :name  "Norwegian Sea"
    :ids   ["AE" "AF"]}
   {:label "BAFFIN-BUCHT und DAVISSTRASSE"
    :name  "Baffin Bay and Davis Strait"
    :ids   ["ÄE" "ÄD"]}
   {:label "HUDSON-BUCHT und HUDSONSTRASSE"
    :name  "Hudson Bay and Hudson Strait"
    :ids   ["ÄC"]}
   {:label "DAVISSTRASSE"
    :name  "Davis Strait"
    :ids   ["ÄB"]}
   {:label "LABRADORSEE"
    :name  "Labrador Sea"
    :ids   ["ÄA" "AH" "AJ"]}
   {:label "OSTSEE"
    :name  "Baltic Sea"
    :ids   ["AG" "AO"]}
   {:label "NORDSEE"
    :name  "North Sea"
    :ids   ["AN"]}
   {:label "BISKAYA und ENGLISCHER KANAL"
    :name  "Bay of Biscay and English Channel"
    :ids   ["BF"]}
   {:label "SANKT-LORENZ-STROM"
    :name  "St. Lawrence River"
    :ids   ["BA"]}
   {:label "MITTELMEER"
    :name  "Mediterranean Sea"
    :ids   ["CH" "CJ" "CK" "CN" "CO" "CP"]}
   {:label "SCHWARZES MEER"
    :name  "Black Sea"
    :ids   ["CL" "CS"]}
   {:label "ROTES MEER"
    :name  "Red Sea"
    :ids   ["CQ" "CR" "MD" "MO"]}
   {:label "KASPISCHES MEER"
    :name  "Caspian Sea"
    :ids   ["CU" "CV"]}
   {:label "PERSISCHER GOLF"
    :name  "Persian Gulf"
    :ids   ["MA" "MB" "ME"]}
   {:label "NORD-ATLANTISCHER OZEAN"
    :name  "North Atlantic Ocean"
    :ids   ["AD" "AK" "AL" "AM" "BB" "BC" "BD" "BE" "CA" "CB" "CC" "CD" "CE" "CF" "CG" "DB" "DC" "DD" "DE" "DF" "DG" "DH" "DJ"]}
   {:label "ZENTRAL-ATLANTISCHER OZEAN"
    :name  "Central Atlantic Ocean"
    :ids   ["DM" "DN" "DO" "DP" "DQ" "DR" "DS" "DT" "DU" "ED" "EE" "EF" "EG" "EH" "EJ" "EK" "EN" "EO" "EP" "EQ" "ER" "ES" "ET"
            "EU" "EV" "EW" "FA" "FB" "FC" "FD" "FE" "FF" "FG" "FH"]}
   {:label "SÜD-ATLANTISCHER OZEAN"
    :name  "South Atlantic Ocean"
    :ids   ["FJ" "FK" "FL" "FM" "FN" "FO" "FP" "FQ" "FR" "FS" "FT" "FU" "FV" "FW" "GA" "GB" "GC" "GD" "GE" "GF" "GG" "GH" "GJ"
            "GK" "GL" "GM" "GN" "GO" "GP" "GQ" "GR" "GS" "GT" "GU" "GV" "GW" "GX" "GY" "GZ" "JJ" "HA" "HB" "HC" "HD" "HE" "HF"
            "HG" "HH" "HJ" "HK" "HL" "HM" "HN"]}
   {:label "GOLF VON MEXIKO"
    :name  "Gulf of Mexico"
    :ids   ["DA" "DK" "DL"]}
   {:label "KARIBISCHES MEER"
    :name  "Caribbean Sea"
    :ids   ["EB" "EC" "EM"]}
   {:label "GOLF VON KALIFORNIEN"
    :name  "Gulf of California"
    :ids   ["QB"]}
   {:label "BERINGMEER"
    :name  "Bering Sea"
    :ids   ["ND" "NE"]}
   {:label "GOLF VON ALASKA"
    :name  "Gulf of Alaska"
    :ids   ["NF"]}
   {:label "JAPANISCHES MEER"
    :name  "Sea of Japan"
    :ids   ["OA" "OB" "OC"]}
   {:label "OST-CHINESISCHES MEER"
    :name  "East China Sea"
    :ids   ["OO" "OP"]}
   {:label "SÜD-CHINESISCHES MEER"
    :name  "South China Sea"
    :ids   ["MM" "MN" "MW" "MX"]}
   {:label "STILLER OZEAN (Nördlicher Teil)"
    :name  "North Pacific Ocean"
    :ids   ["NA" "NB" "NC" "NG" "NH" "NJ" "NK" "NL" "NM" "NN" "NO" "NP" "NQ" "NR" "NS" "NT" "NU" "NV" "NW" "NX" "NY" "NZ" "OD"
            "OE" "OF" "OG" "OH" "OJ" "OK" "OL" "OM" "ON" "OQ" "OR" "OS" "OT" "OU" "OV" "OW" "OX" "OY" "OZ" "QA"]}
   {:label "STILLER OZEAN (Mittlerer Teil)"
    :name  "Central Pacific Ocean"
    :ids   ["QC" "QD" "QE" "QF" "QG" "QH" "QJ" "QK" "QL" "QM" "QN" "QO" "QP" "QQ" "QR" "QS" "QT" "QU" "QV" "QW" "QX" "QY" "QZ"
            "RA" "RB" "RC" "RD" "RE" "RF" "RG" "RH" "RJ" "RK" "RL" "RM" "RN" "PA" "EA" "RO" "RP" "RQ" "RR" "RS" "RT" "RU" "RV"
            "RW" "RX" "RY" "RZ" "SA" "SB" "SC" "SD" "SE" "SF" "PB" "PC" "EL" "SG" "SH" "SJ" "SK" "SL" "SM" "SN" "SO" "SP" "SQ"
            "SR" "SS" "ST" "SU" "SV" "SW" "SX" "SY" "PD" "PE" "PF" "PG" "PH" "PJ"]}
   {:label "STILLER OZEAN (Südlicher Teil)"
    :name  "South Pacific Ocean"
    :ids   ["TA" "TB" "TC" "TD" "TE" "TF" "TG" "TH" "TJ" "TK" "TL" "TM" "TN" "TO" "TP" "TQ" "TR" "TV" "TW" "TX" "TY" "TZ" "UA"
            "UB" "UC" "UD" "UE" "UF" "UG" "UH" "UJ" "UK" "UL" "PK" "PL" "PM" "PN" "UP" "UQ" "UR" "US" "UT" "UU" "UV" "UW" "UX"
            "UY" "UZ" "VA" "PO" "PP" "PQ" "PR" "VD" "VE" "VF" "VG" "VH" "VJ" "VK" "VL" "VM" "VN" "VO" "VP" "PS" "PT" "PU" "VR"
            "VS" "VT" "VV" "VW" "VX" "VY" "VZ" "WA" "PV" "PW" "PX" "WC" "WD" "WE" "WF" "WG" "WH" "WJ" "WK" "WL" "WM" "PY" "PZ"
            "WC" "WD" "WE" "WF" "WG" "WH" "WJ" "WK" "WL" "WM" "PY" "PZ" "WO" "WP" "WQ" "WR" "WS" "WT" "WU" "WV" "WW"]}
   {:label "INDISCHER OZEAN"
    :name "Indian Ocean"
    :ids   ["MF" "MG" "MH" "MJ" "MK" "ML" "MP" "MQ" "MR" "MS" "MT" "MU" "MV" "MY" "MZ" "LA" "LB" "LC" "LD" "LE" "LF" "LG" "LH"
            "LJ" "LK" "LL" "LM" "LN" "LO" "LP" "LQ" "LR" "LS" "LT" "LU" "LV" "LW" "LX" "LY" "LZ" "KA" "KB" "KC" "KD" "KE" "KF"
            "KG" "KH" "KJ" "KK" "KL" "KM" "KN" "KO" "KP" "KQ" "KR" "KS" "KT" "KU" "KV" "KW" "KX" "KY" "KZ" "SZ" "TT" "TU" "JA"
            "JB" "JC" "JD" "JE" "JF" "JG" "JH" "JK" "JL" "JM" "JN" "JO" "JP" "JQ" "JR" "JS" "JT" "JU" "JV" "JW" "JX" "JY" "JZ"
            "VB" "VC" "VQ" "WB" "HO" "HP" "HQ" "HR" "HS" "HT" "HU"]}
   {:label "Weltkarte"
    :ids   ["BG" "BH" "BJ" "BK" "AP" "AQ" "CM" "UN" "UO"]}])