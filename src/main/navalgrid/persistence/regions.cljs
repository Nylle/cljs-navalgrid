(ns navalgrid.persistence.regions)

(def all
  [{:name "Nördliches Eismeer"
    :ids  ["ÄJ" "ÄH" "ÄG" "ÄF" "AA" "AB" "AC" "AT" "AS" "AW" "AU" "AR" "XL" "XM" "XN" "XA" "XC" "XE" "XJ" "XK" "XG" "XH" "XO"
           "XP" "XQ" "XR" "XZ" "XB" "XD" "XF" "YD" "YE" "YB" "YC"]}
   {:name "Europäisches Nordmeer"
    :ids  ["AE" "AF"]}
   {:name "Baffin-Bucht und Davisstraße"
    :ids  ["ÄE" "ÄD"]}
   {:name "Hudson-Bucht und Hudsonstraße"
    :ids  ["ÄC"]}
   {:name "Davisstraße"
    :ids  ["ÄB"]}
   {:name "Labradorsee"
    :ids  ["ÄA" "AH" "AJ"]}
   {:name "Ostsee"
    :ids  ["AG" "AO"]}
   {:name "Nordsee"
    :ids  ["AN"]}
   {:name "Biskaya und Englischer Kanal"
    :ids  ["BF"]}
   {:name "Sankt-Lorenz-Strom"
    :ids  ["BA"]}
   {:name "Mittelmeer"
    :ids  ["CH" "CJ" "CK" "CN" "CO" "CP"]}
   {:name "Schwarzes Meer"
    :ids  ["CL" "CS"]}
   {:name "Rotes Meer"
    :ids  ["CQ" "CR" "MD" "MO"]}
   {:name "Kaspisches Meer"
    :ids  ["CU" "CV"]}
   {:name "Perischer Golf"
    :ids  ["MA" "MB" "ME"]}
   {:name "Nord-Atlantischer Ozean"
    :ids  ["AD" "AK" "AL" "AM" "BB" "BC" "BD" "BE" "CA" "CB" "CC" "CD" "CE" "CF" "CG" "DB" "DC" "DD" "DE" "DF" "DG" "DH" "DJ"]}
   {:name "Zentral-Atlantischer Ozean"
    :ids  ["DM" "DN" "DO" "DP" "DQ" "DR" "DS" "DT" "DU" "ED" "EE" "EF" "EG" "EH" "EJ" "EK" "EN" "EO" "EP" "EQ" "ER" "ES" "ET"
           "EU" "EV" "EW" "FA" "FB" "FC" "FD" "FE" "FF" "FG" "FH"]}
   {:name "Süd-Atlantischer Ozean"
    :ids  ["FJ" "FK" "FL" "FM" "FN" "FO" "FP" "FQ" "FR" "FS" "FT" "FU" "FV" "FW" "GA" "GB" "GC" "GD" "GE" "GF" "GG" "GH" "GJ"
           "GK" "GL" "GM" "GN" "GO" "GP" "GQ" "GR" "GS" "GT" "GU" "GV" "GW" "GX" "GY" "GZ" "JJ" "HA" "HB" "HC" "HD" "HE" "HF"
           "HG" "HH" "HJ" "HK" "HL" "HM" "HN"]}
   {:name "Golf von Mexiko"
    :ids  ["DA" "DK" "DL"]}
   {:name "Karibisches Meer"
    :ids  ["EB" "EC" "EM"]}
   {:name "Golf von Kalifornien"
    :ids  ["QB"]}
   {:name "Beringmeer"
    :ids  ["ND" "NE"]}
   {:name "Golf von Alaska"
    :ids  ["NF"]}
   {:name "Japanisches Meer"
    :ids  ["OA" "OB" "OC"]}
   {:name "Ost-Chinesisches Meer"
    :ids  ["OO" "OP"]}
   {:name "Süd-Chinesisches Meer"
    :ids  ["MM" "MN" "MW" "MX"]}
   {:name "Stiller Ozean (Nördlicher Teil)"
    :ids  ["NA" "NB" "NC" "NG" "NH" "NJ" "NK" "NL" "NM" "NN" "NO" "NP" "NQ" "NR" "NS" "NT" "NU" "NV" "NW" "NX" "NY" "NZ" "OD"
           "OE" "OF" "OG" "OH" "OJ" "OK" "OL" "OM" "ON" "OQ" "OR" "OS" "OT" "OU" "OV" "OW" "OX" "OY" "OZ" "QA"]}
   {:name "Stiller Ozean (Mittlerer Teil)"
    :ids  ["QC" "QD" "QE" "QF" "QG" "QH" "QJ" "QK" "QL" "QM" "QN" "QO" "QP" "QQ" "QR" "QS" "QT" "QU" "QV" "QW" "QX" "QY" "QZ"
           "RA" "RB" "RC" "RD" "RE" "RF" "RG" "RH" "RJ" "RK" "RL" "RM" "RN" "PA" "EA" "RO" "RP" "RQ" "RR" "RS" "RT" "RU" "RV"
           "RW" "RX" "RY" "RZ" "SA" "SB" "SC" "SD" "SE" "SF" "PB" "PC" "EL" "SG" "SH" "SJ" "SK" "SL" "SM" "SN" "SO" "SP" "SQ"
           "SR" "SS" "ST" "SU" "SV" "SW" "SX" "SY" "PD" "PE" "PF" "PG" "PH" "PJ"]}
   {:name "Stiller Ozean (Südlicher Teil)"
    :ids  ["TA" "TB" "TC" "TD" "TE" "TF" "TG" "TH" "TJ" "TK" "TL" "TM" "TN" "TO" "TP" "TQ" "TR" "TV" "TW" "TX" "TY" "TZ" "UA"
           "UB" "UC" "UD" "UE" "UF" "UG" "UH" "UJ" "UK" "UL" "PK" "PL" "PM" "PN" "UP" "UQ" "UR" "US" "UT" "UU" "UV" "UW" "UX"
           "UY" "UZ" "VA" "PO" "PP" "PQ" "PR" "VD" "VE" "VF" "VG" "VH" "VJ" "VK" "VL" "VM" "VN" "VO" "VP" "PS" "PT" "PU" "VR"
           "VS" "VT" "VV" "VW" "VX" "VY" "VZ" "WA" "PV" "PW" "PX" "WC" "WD" "WE" "WF" "WG" "WH" "WJ" "WK" "WL" "WM" "PY" "PZ"
           "WC" "WD" "WE" "WF" "WG" "WH" "WJ" "WK" "WL" "WM" "PY" "PZ" "WO" "WP" "WQ" "WR" "WS" "WT" "WU" "WV" "WW"]}
   {:name "Indischer Ozean"
    :ids  ["MF" "MG" "MH" "MJ" "MK" "ML" "MP" "MQ" "MR" "MS" "MT" "MU" "MV" "MY" "MZ" "LA" "LB" "LC" "LD" "LE" "LF" "LG" "LH"
           "LJ" "LK" "LL" "LM" "LN" "LO" "LP" "LQ" "LR" "LS" "LT" "LU" "LV" "LW" "LX" "LY" "LZ" "KA" "KB" "KC" "KD" "KE" "KF"
           "KG" "KH" "KJ" "KK" "KL" "KM" "KN" "KO" "KP" "KQ" "KR" "KS" "KT" "KU" "KV" "KW" "KX" "KY" "KZ" "SZ" "TT" "TU" "JA"
           "JB" "JC" "JD" "JE" "JF" "JG" "JH" "JK" "JL" "JM" "JN" "JO" "JP" "JQ" "JR" "JS" "JT" "JU" "JV" "JW" "JX" "JY" "JZ"
           "VB" "VC" "VQ" "WB" "HO" "HP" "HQ" "HR" "HS" "HT" "HU"]}
   {:name "Weltkarte"
    :ids  ["BG" "BH" "BJ" "BK" "AP" "AQ" "CM" "UN" "UO"]}])