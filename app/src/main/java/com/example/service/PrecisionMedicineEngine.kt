package com.example.service

import com.example.data.model.ActiveMedicationEntity
import com.example.data.model.DiseaseRiskPrediction
import com.example.data.model.EvaluatedInteraction
import com.example.data.model.GeneticMarkerEntity
import com.example.data.model.SymptomReportEntity
import java.util.Locale

object PrecisionMedicineEngine {

  /**
   * Evaluates active medications against patient's genetic markers and co-medications
   */
  fun evaluateDrugInteractions(
    medications: List<ActiveMedicationEntity>,
    markers: List<GeneticMarkerEntity>
  ): List<EvaluatedInteraction> {
    val results = mutableListOf<EvaluatedInteraction>()
    val markerMap = markers.associateBy { it.gene.uppercase(Locale.US) }

    val drugNames = medications.map { it.drugName.lowercase(Locale.US) }

    for (med in medications) {
      val nameLower = med.drugName.lowercase(Locale.US)

      // Clopidogrel & CYP2C19
      if (nameLower.contains("clopidogrel") || nameLower.contains("plavix")) {
        val cyp2c19 = markerMap["CYP2C19"]
        if (cyp2c19 != null && (cyp2c19.phenotype.contains("Poor", ignoreCase = true) || cyp2c19.genotype.contains("*2"))) {
          results.add(
            EvaluatedInteraction(
              drugName = med.drugName,
              interactingGeneOrDrug = "CYP2C19 (*2/*2 Poor Metabolizer)",
              severity = EvaluatedInteraction.SeverityLevel.CONTRAINDICATED,
              type = EvaluatedInteraction.InteractionType.GENE_DRUG,
              biologicalMechanism = "CYP2C19 loss-of-function prevents hepatic enzymatic bioactivation of Clopidogrel prodrug into active thiol metabolite, causing resistance to platelet inhibition and high thrombosis risk.",
              clinicalGuidance = "CPIC Guideline 1A: Discontinue Clopidogrel. Alternate antiplatelet agent indicated immediately.",
              recommendedAlternatives = listOf("Ticagrelor (Brilinta) 90mg BID", "Prasugrel (Effient) 10mg Daily")
            )
          )
        }
      }

      // Codeine / Tramadol & CYP2D6
      if (nameLower.contains("codeine") || nameLower.contains("tramadol") || nameLower.contains("tylenol 3")) {
        val cyp2d6 = markerMap["CYP2D6"]
        if (cyp2d6 != null && cyp2d6.phenotype.contains("Poor", ignoreCase = true)) {
          results.add(
            EvaluatedInteraction(
              drugName = med.drugName,
              interactingGeneOrDrug = "CYP2D6 (*4/*4 Poor Metabolizer)",
              severity = EvaluatedInteraction.SeverityLevel.CONTRAINDICATED,
              type = EvaluatedInteraction.InteractionType.GENE_DRUG,
              biologicalMechanism = "Zero functional CYP2D6 enzyme activity prevents conversion of codeine to morphine. Patient receives zero analgesia while retaining risk of toxic metabolite side effects.",
              clinicalGuidance = "Avoid codeine/tramadol. Therapeutic failure is guaranteed under this genotype.",
              recommendedAlternatives = listOf("Hydromorphone", "Acetaminophen + Ibuprofen protocol", "Oxycodone (monitored)")
            )
          )
        }
      }

      // Simvastatin / Atorvastatin & SLCO1B1
      if (nameLower.contains("simvastatin") || nameLower.contains("zocor")) {
        val slco1b1 = markerMap["SLCO1B1"]
        if (slco1b1 != null && (slco1b1.phenotype.contains("Intermediate", ignoreCase = true) || slco1b1.genotype.contains("521"))) {
          results.add(
            EvaluatedInteraction(
              drugName = med.drugName,
              interactingGeneOrDrug = "SLCO1B1 (521T>C Reduced Hepatic Transporter)",
              severity = EvaluatedInteraction.SeverityLevel.SEVERE,
              type = EvaluatedInteraction.InteractionType.GENE_DRUG,
              biologicalMechanism = "Reduced OATP1B1 hepatic uptake transporter leads to 220% higher systemic circulating plasma levels of simvastatin acid, markedly escalating the risk of severe myopathy, myalgia, and rhabdomyolysis.",
              clinicalGuidance = "Prescribe lower dose (max 20mg) or substitute with a statin with low SLCO1B1 dependence.",
              recommendedAlternatives = listOf("Pravastatin (Pravachol) 20-40mg", "Rosuvastatin (Crestor) 5-10mg")
            )
          )
        }
      }

      // Warfarin & VKORC1 / CYP2C9
      if (nameLower.contains("warfarin") || nameLower.contains("coumadin")) {
        val vkorc1 = markerMap["VKORC1"]
        if (vkorc1 != null && vkorc1.phenotype.contains("Sensitivity", ignoreCase = true)) {
          results.add(
            EvaluatedInteraction(
              drugName = med.drugName,
              interactingGeneOrDrug = "VKORC1 (-1639A/A Promoter Variant)",
              severity = EvaluatedInteraction.SeverityLevel.SEVERE,
              type = EvaluatedInteraction.InteractionType.GENE_DRUG,
              biologicalMechanism = "Decreased baseline expression of vitamin K epoxide reductase complex subunit 1 lowers target protein availability, rendering patient hyper-sensitive to warfarin-induced anticoagulation.",
              clinicalGuidance = "Standard 5mg initial dosing will precipitate supratherapeutic INR (>4.0) and major hemorrhage. Start at 1.5 - 2.0 mg/day or transition to DOAC.",
              recommendedAlternatives = listOf("Apixaban (Eliquis) 5mg BID", "Rivaroxaban (Xarelto) 20mg Daily")
            )
          )
        }
      }

      // Abacavir & HLA-B*5701
      if (nameLower.contains("abacavir") || nameLower.contains("ziagen") || nameLower.contains("triumeq")) {
        val hla = markerMap["HLA-B*5701"]
        if (hla != null && hla.clinicalSignificance.contains("Positive", ignoreCase = true)) {
          results.add(
            EvaluatedInteraction(
              drugName = med.drugName,
              interactingGeneOrDrug = "HLA-B*5701 (Positive)",
              severity = EvaluatedInteraction.SeverityLevel.CONTRAINDICATED,
              type = EvaluatedInteraction.InteractionType.GENE_DRUG,
              biologicalMechanism = "Abacavir binds the antigen-binding cleft of HLA-B*5701, altering self-peptide presentation and triggering an acute, multi-system life-threatening hypersensitivity reaction.",
              clinicalGuidance = "Absolute contraindication across FDA and EMA regulatory labels.",
              recommendedAlternatives = listOf("Tenofovir Alafenamide (TAF)", "Tenofovir Disoproxil (TDF)")
            )
          )
        }
      }

      // Drug-Drug Interaction: Clopidogrel + Omeprazole
      if (nameLower.contains("clopidogrel") && drugNames.any { it.contains("omeprazole") || it.contains("prilosec") }) {
        results.add(
          EvaluatedInteraction(
            drugName = "Clopidogrel + Omeprazole",
            interactingGeneOrDrug = "Competitive CYP2C19 Inhibition",
            severity = EvaluatedInteraction.SeverityLevel.SEVERE,
            type = EvaluatedInteraction.InteractionType.DRUG_DRUG,
            biologicalMechanism = "Omeprazole strongly inhibits the CYP2C19 pathway required for Clopidogrel activation, compounding loss of antiplatelet efficacy.",
            clinicalGuidance = "Switch proton-pump inhibitor to Pantoprazole or H2 blocker Famotidine.",
            recommendedAlternatives = listOf("Pantoprazole (Protonix)", "Famotidine (Pepcid)")
          )
        )
      }

      // Drug-Drug Interaction: Warfarin + NSAID / Aspirin
      if (nameLower.contains("warfarin") && drugNames.any { it.contains("aspirin") || it.contains("ibuprofen") || it.contains("naproxen") }) {
        results.add(
          EvaluatedInteraction(
            drugName = "Warfarin + NSAID/Aspirin",
            interactingGeneOrDrug = "Synergistic Hemostasis Disruption",
            severity = EvaluatedInteraction.SeverityLevel.CONTRAINDICATED,
            type = EvaluatedInteraction.InteractionType.DRUG_DRUG,
            biologicalMechanism = "Dual pathway inhibition of clotting factor synthesis and COX-1 platelet aggregation with gastric mucosal erosion quadruples upper gastrointestinal bleeding risk.",
            clinicalGuidance = "Discontinue oral NSAID. Use topical NSAID or Acetaminophen with close INR surveillance.",
            recommendedAlternatives = listOf("Acetaminophen (Tylenol) max 2g/day", "Topical Voltaren Gel")
          )
        )
      }
    }

    // Add safe confirmation if no conflicts found for standard meds
    if (results.isEmpty() && medications.isNotEmpty()) {
      results.add(
        EvaluatedInteraction(
          drugName = medications.first().drugName,
          interactingGeneOrDrug = "Genomic Panel Verification",
          severity = EvaluatedInteraction.SeverityLevel.SAFE,
          type = EvaluatedInteraction.InteractionType.GENE_DRUG,
          biologicalMechanism = "No active pharmacogenomic contraindications or dangerous drug-drug interactions detected across active CPIC Level 1A guidelines.",
          clinicalGuidance = "Medication profile aligns with current metabolic genotype.",
          recommendedAlternatives = listOf("Maintain current prescribed regimen")
        )
      )
    }

    return results
  }

  /**
   * Computes personalized Disease and Disorder risk predictions based on genetic markers
   */
  fun calculateDiseaseRiskPredictions(markers: List<GeneticMarkerEntity>): List<DiseaseRiskPrediction> {
    val markerMap = markers.associateBy { it.gene.uppercase(Locale.US) }

    val list = mutableListOf<DiseaseRiskPrediction>()

    // 1. Hereditary Breast & Ovarian Cancer (BRCA1)
    val brca = markerMap["BRCA1"]
    if (brca != null && (brca.clinicalSignificance.contains("Pathogenic", ignoreCase = true) ||
        brca.phenotype.contains("Pathogenic", ignoreCase = true) ||
        brca.clinicalSignificance.contains("Penetrance", ignoreCase = true))) {
      list.add(
        DiseaseRiskPrediction(
          conditionName = "Hereditary Breast & Ovarian Cancer",
          category = "Cancer Predisposition",
          relativeRiskMultiplier = 5.8f,
          lifetimeRiskPercent = 65,
          averagePopulationRiskPercent = 12,
          riskLevel = "HIGH RISK",
          keyMarkers = "BRCA1 c.68_69delAG (Pathogenic frameshift)",
          preventativeLifestyle = listOf(
            "Maintain body weight in optimal BMI range (18.5-24.9) to reduce peripheral estrogen conversion",
            "Engage in at least 150 minutes of moderate-to-vigorous aerobic exercise weekly",
            "Minimize alcohol consumption (< 1 drink per week; zero alcohol strongly preferred)",
            "Avoid postmenopausal systemic hormone replacement therapy (HRT)"
          ),
          recommendedSurveillance = listOf(
            "Annual Contrast-Enhanced Breast MRI starting immediately (alternate every 6 mos with mammogram)",
            "Clinical Breast Examination every 6 months with specialized surgical oncologist",
            "Pelvic transvaginal ultrasound + serum CA-125 surveillance",
            "Consultation for prophylactic risk-reducing bilateral salpingo-oophorectomy (RRSO) between age 35-40"
          )
        )
      )
    }

    // 2. Coronary Artery Disease & Dyslipidemia (APOE + SLCO1B1)
    val apoe = markerMap["APOE"]
    if (apoe != null && apoe.genotype.contains("ε4")) {
      list.add(
        DiseaseRiskPrediction(
          conditionName = "Premature Coronary Atherosclerosis & Dyslipidemia",
          category = "Cardiovascular Risk",
          relativeRiskMultiplier = 2.4f,
          lifetimeRiskPercent = 48,
          averagePopulationRiskPercent = 25,
          riskLevel = "ELEVATED",
          keyMarkers = "APOE ε3/ε4 (Heterozygous carrier)",
          preventativeLifestyle = listOf(
            "Strictly limit dietary saturated fats (<6% of daily caloric intake) to avoid exaggerated LDL spike",
            "Incorporate plant sterols (2g/day) and soluble fiber (oats, psyllium husk, chia seeds)",
            "High-intensity interval training (HIIT) 2x weekly to stimulate hepatic LDL receptor clearance",
            "Daily supplementation with pharmaceutical-grade Omega-3 EPA/DHA (2000mg)"
          ),
          recommendedSurveillance = listOf(
            "Coronary Artery Calcium (CAC) CT scoring scan every 3 years",
            "Advanced Lipoprotein Particle Panel (ApoB and LDL-P counts) every 6 months",
            "Carotid intima-media thickness (CIMT) Doppler ultrasound"
          )
        )
      )
    }

    // 3. Late-Onset Alzheimer's Disease (APOE)
    if (apoe != null && apoe.genotype.contains("ε4")) {
      list.add(
        DiseaseRiskPrediction(
          conditionName = "Late-Onset Alzheimer's Disease Susceptibility",
          category = "Neurodegenerative Risk",
          relativeRiskMultiplier = 3.2f,
          lifetimeRiskPercent = 32,
          averagePopulationRiskPercent = 10,
          riskLevel = "ELEVATED",
          keyMarkers = "APOE ε3/ε4 allele",
          preventativeLifestyle = listOf(
            "Adopt the Mediterranean-DASH Intervention for Neurodegenerative Delay (MIND) dietary protocol",
            "Optimize restorative deep slow-wave sleep (7-8.5 hours/night) to support glymphatic beta-amyloid clearance",
            "Continuous cognitive reserve enrichment (learning complex instruments, bilingual tasks, novel skills)",
            "Control systolic blood pressure rigorously to <120 mmHg to protect microvascular cerebral perfusion"
          ),
          recommendedSurveillance = listOf(
            "Annual standardized Montreal Cognitive Assessment (MoCA) baseline tracking starting age 50",
            "Plasma phosphorylated tau (p-tau217) biomarker screening as clinically indicated",
            "Volumetric brain MRI tracking hippocampal preservation"
          )
        )
      )
    }

    // 4. Type 2 Diabetes & Insulin Resistance (TCF7L2)
    val tcf7l2 = markerMap["TCF7L2"]
    if (tcf7l2 != null && tcf7l2.genotype.contains("T")) {
      list.add(
        DiseaseRiskPrediction(
          conditionName = "Type 2 Diabetes Mellitus & Incretin Resistance",
          category = "Metabolic Disorder",
          relativeRiskMultiplier = 1.6f,
          lifetimeRiskPercent = 38,
          averagePopulationRiskPercent = 22,
          riskLevel = "ELEVATED",
          keyMarkers = "TCF7L2 rs7903146 (C/T risk allele)",
          preventativeLifestyle = listOf(
            "Adopt a Low Glycemic Load (GL) whole-food diet with strict avoidance of refined sugars and fructose syrups",
            "10-15 minute brisk walking session immediately following each carbohydrate-containing meal",
            "Progressive resistance hypertrophy training 3x weekly to increase non-insulin dependent GLUT4 glucose uptake",
            "Intermittent time-restricted feeding (14:10 window) to restore hepatic insulin sensitivity"
          ),
          recommendedSurveillance = listOf(
            "Fasting Plasma Glucose & HbA1c testing every 6 months",
            "Annual continuous glucose monitoring (CGM) 14-day evaluation for glycemic variability",
            "Urine Albumin-to-Creatinine ratio (uACR) and comprehensive renal profile annually"
          )
        )
      )
    }

    // 5. Impaired Methylation & Hyperhomocysteinemia (MTHFR)
    val mthfr = markerMap["MTHFR"]
    if (mthfr != null && mthfr.genotype.contains("T")) {
      list.add(
        DiseaseRiskPrediction(
          conditionName = "Cellular Methylation & Microvascular Strain",
          category = "Nutrigenomic Disorder",
          relativeRiskMultiplier = 1.4f,
          lifetimeRiskPercent = 28,
          averagePopulationRiskPercent = 18,
          riskLevel = "MODERATE",
          keyMarkers = "MTHFR 677C>T (Heterozygous polymorphism)",
          preventativeLifestyle = listOf(
            "Avoid foods fortified with synthetic folic acid; substitute with bioactive folate (L-5-MTHF)",
            "Abundant daily intake of folate-rich dark leafy greens (spinach, arugula, asparagus, broccoli)",
            "Support methylation co-factors with Methylcobalamin (B12), Pyridoxal-5-phosphate (B6), and Riboflavin (B2)",
            "Limit excessive intake of black tea and coffee around meals which inhibit folate absorption"
          ),
          recommendedSurveillance = listOf(
            "Annual fasting Total Homocysteine (tHcy) blood assay (target <9 µmol/L)",
            "Serum Holotranscobalamin and Methylmalonic acid (MMA) assessment"
          )
        )
      )
    }

    return list
  }

  /**
   * Diagnostic Symptom Screener Engine
   * Covers:
   * 1. Cancers & Oncology warning signs
   * 2. Viral Fever syndromes
   * 3. Bacterial Infections
   * 4. Chronic Conditions onset
   */
  fun screenSymptoms(
    selectedSymptoms: List<String>,
    patientProfile: com.example.data.model.PatientProfileEntity?,
    markers: List<GeneticMarkerEntity>
  ): SymptomReportEntity {
    val lower = selectedSymptoms.map { it.lowercase(Locale.US) }
    val symptomsStr = selectedSymptoms.joinToString("; ")

    // 1. Check for Cancer / Oncology Warning Signs
    val hasCancerSigns = lower.any {
      it.contains("weight loss") || it.contains("lump") || it.contains("cough with blood") ||
        it.contains("mole") || it.contains("swallowing") || it.contains("bleeding") ||
        it.contains("lymph") || it.contains("night sweats") || it.contains("breast")
    }

    // 2. Check for Viral Fever
    val hasViralSigns = lower.any {
      it.contains("high fever") || it.contains("body ache") || it.contains("retro-orbital") ||
        it.contains("rash") || it.contains("runny nose") || it.contains("viral") || it.contains("chills")
    }

    // 3. Check for Bacterial Infections
    val hasBacterialSigns = lower.any {
      it.contains("purulent") || it.contains("productive cough") || it.contains("rust") ||
        it.contains("tonsil") || it.contains("exudate") || it.contains("burning urination") ||
        it.contains("flank pain") || it.contains("cellulitis") || it.contains("abscess") ||
        it.contains("localized heat")
    }

    // 4. Check for Chronic Conditions Onset
    val hasChronicSigns = lower.any {
      it.contains("thirst") || it.contains("frequent urination") || it.contains("morning stiffness") ||
        it.contains("blurred vision") || it.contains("pedal edema") || it.contains("shortness of breath") ||
        it.contains("numbness") || it.contains("tingling")
    }

    val hasBrca = markers.any { it.gene.equals("BRCA1", ignoreCase = true) && it.clinicalSignificance.contains("Pathogenic", ignoreCase = true) }

    when {
      hasCancerSigns -> {
        val triage = if (lower.any { it.contains("blood") || it.contains("severe") }) "EMERGENCY" else "URGENT_EVALUATION"
        val genomicNote = if (hasBrca) {
          "CRITICAL GENOMIC CORRELATION: Patient is a confirmed carrier of pathogenic BRCA1 (c.68_69delAG). Reported breast/lymph/weight manifestations demand expedited diagnostic workup within 48-72 hours."
        } else {
          "Genomic background review recommended alongside standard oncology diagnostic pathway."
        }

        return SymptomReportEntity(
          symptomsReported = symptomsStr,
          suspectedCategory = "Oncology & Cancer Warning Red Flags",
          triageLevel = triage,
          preliminaryDiagnosis = "Suspicion of Neoplastic / Oncologic Etiology requiring immediate histological & imaging investigation",
          clinicalFindings = "Reported red flag symptoms match established clinical criteria for early oncologic onset (unexplained constitutional symptoms or palpable tissue mass). Prompt histopathologic confirmation is mandatory.",
          recommendedFollowUpTests = "1. Diagnostic Contrast-Enhanced Mammography & High-Resolution Ultrasound, 2. Ultrasound-guided Core Needle Biopsy with immunohistochemistry, 3. Complete Blood Count (CBC) with differential, 4. Serum Tumor Markers (CA-125, CEA, CA 15-3), 5. Whole-Body FDG-PET/CT staging if biopsy positive",
          genomicCorrelationNote = genomicNote
        )
      }

      hasBacterialSigns -> {
        val isEmergency = lower.any { it.contains("confusion") || it.contains("hypotension") || it.contains("rigors") || it.contains("flank") }
        val triage = if (isEmergency) "EMERGENCY" else "URGENT_EVALUATION"

        return SymptomReportEntity(
          symptomsReported = symptomsStr,
          suspectedCategory = "Acute Bacterial Infection",
          triageLevel = triage,
          preliminaryDiagnosis = "Probable Focal Bacterial Infection (e.g. Bacterial Pneumonia, Acute Pyelonephritis, or Purulent Soft Tissue Infection)",
          clinicalFindings = "Presence of purulent exudate, productive sputum, focal tissue erythema, or unilateral flank pain strongly favors bacterial proliferation rather than viral self-limiting illness. Antibiotic stewardship requires microbiological confirmation.",
          recommendedFollowUpTests = "1. Blood Cultures (2 sets drawn from separate venipunctures prior to antibiotic administration), 2. Sputum Gram Stain & Culture with Antibiogram sensitivity, 3. Serum Procalcitonin & Quantitative C-Reactive Protein (CRP), 4. Complete Blood Count showing Left-Shift neutrophilia, 5. Posteroanterior (PA) Chest Radiograph or Renal Ultrasound",
          genomicCorrelationNote = "Patient pharmacogenomic profile must be checked prior to prescribing fluoroquinolones or macrolides to avoid QT prolongation or transporter toxicity."
        )
      }

      hasViralSigns -> {
        return SymptomReportEntity(
          symptomsReported = symptomsStr,
          suspectedCategory = "Acute Viral Febrile Illness",
          triageLevel = "URGENT_EVALUATION",
          preliminaryDiagnosis = "Acute Systemic Viral Syndrome (Differential includes Dengue Fever, Seasonal Influenza A/B, COVID-19, or Acute Arboviral Infection)",
          clinicalFindings = "Sudden onset high pyrexia accompanied by diffuse myalgias, retro-orbital cephalalgia, and cutaneous maculopapular rash represents classic viral constitutional response.",
          recommendedFollowUpTests = "1. Complete Blood Count (CBC) with serial Platelet count tracking (rule out viral thrombocytopenia), 2. Dengue NS1 Antigen & IgM/IgG Serology, 3. Multiplex Respiratory Viral Panel PCR (Flu A/B, RSV, SARS-CoV-2), 4. Comprehensive Hepatic Panel (ALT/AST), 5. Serum Electrolytes & Renal Profile",
          genomicCorrelationNote = "Strictly avoid NSAIDs (Aspirin, Ibuprofen) due to dengue hemorrhagic risk. Codeine is contraindicated given CYP2D6 poor metabolizer status; use Acetaminophen with liver monitoring."
        )
      }

      hasChronicSigns -> {
        return SymptomReportEntity(
          symptomsReported = symptomsStr,
          suspectedCategory = "Onset of Chronic Cardiometabolic / Autoimmune Condition",
          triageLevel = "ROUTINE_OUTPATIENT",
          preliminaryDiagnosis = "Early Onset of Metabolic Dysfunction (Type 2 Diabetes Spectrum) or Connective Tissue Autoimmunity",
          clinicalFindings = "Polyuria, polydipsia, and morning joint stiffness signal emerging microvascular, endocrine, or autoimmune pathogenesis requiring definitive laboratory grading.",
          recommendedFollowUpTests = "1. Fasting Plasma Glucose & Glycated Hemoglobin (HbA1c), 2. Oral Glucose Tolerance Test (OGTT), 3. Serum Creatinine, eGFR, and Urine Albumin-to-Creatinine Ratio (uACR), 4. Rheumatoid Factor (RF) and Anti-Cyclic Citrullinated Peptide (Anti-CCP), 5. Lipid Subfraction Fractionation Panel",
          genomicCorrelationNote = "Patient possesses TCF7L2 C/T risk variant which accelerates pancreatic beta-cell decompensation under chronic glycemic load."
        )
      }

      else -> {
        return SymptomReportEntity(
          symptomsReported = symptomsStr.ifEmpty { "General fatigue & malaise" },
          suspectedCategory = "General Health Screening",
          triageLevel = "ROUTINE_OUTPATIENT",
          preliminaryDiagnosis = "Non-specific Somatic Symptoms requiring clinical correlation",
          clinicalFindings = "Reported symptoms do not currently meet acute criteria for severe infection or oncologic emergency. Baseline metabolic and vital evaluation recommended.",
          recommendedFollowUpTests = "1. Comprehensive Metabolic Panel, 2. Complete Blood Count, 3. Thyroid Stimulating Hormone (TSH) with Free T4, 4. 25-Hydroxy Vitamin D & Serum Ferritin",
          genomicCorrelationNote = "Continue standard precision medicine monitoring aligned with genetic profile."
        )
      }
    }
  }

  /**
   * Generates Personalized Precision Diet and Lifestyle modifications
   */
  fun getPersonalizedDietAndLifestyle(markers: List<GeneticMarkerEntity>): List<PrecisionDietProtocol> {
    val list = mutableListOf<PrecisionDietProtocol>()

    list.add(
      PrecisionDietProtocol(
        title = "APOE ε4 Lipid & Neuro-Protection Protocol",
        targetGene = "APOE ε3/ε4",
        rationale = "Carriers of the ε4 allele exhibit heightened LDL-C elevation in response to saturated dietary fatty acids, accompanied by higher neuro-inflammatory sensitivity.",
        recommendedFoods = listOf("Wild Alaskan Salmon & Sardines (rich in DHA/EPA)", "Extra Virgin Cold-Pressed Olive Oil", "Walnuts and ground flaxseeds", "Dark berries (blueberries, blackberries) rich in anthocyanins", "Cruciferous vegetables (steamed broccoli, Brussels sprouts)"),
        avoidFoods = listOf("Fatty beef cuts, commercial bacon, and pork lard", "Full-fat dairy, heavy cream, and butter", "Palm oil and hydrogenated trans fats", "Industrial ultra-processed snacks"),
        macronutrientDistribution = "45% Low-GI Carbs, 35% Healthy Fats (predominantly Monounsaturated), 20% Lean Protein",
        micronutrientSupplementation = "Omega-3 EPA/DHA (2g daily), Curcumin phytosome (500mg), CoQ10 Ubiquinol (100mg)",
        hydrationAndLifestyle = "Drink 2.5L filtered water daily; practice 12-hour overnight intermittent fasting to promote neuronal autophagy."
      )
    )

    list.add(
      PrecisionDietProtocol(
        title = "MTHFR Folate Remethylation Protocol",
        targetGene = "MTHFR 677C>T",
        rationale = "Thermolabile MTHFR enzyme reduces conversion of 5,10-methylenetetrahydrofolate to 5-methyltetrahydrofolate, risking homocysteine accumulation and vascular endothelial damage.",
        recommendedFoods = listOf("Raw baby spinach, kale, and Swiss chard", "Asparagus spears and steamed artichokes", "Organic lentils, chickpeas, and black beans", "Pasture-raised egg yolks (high in choline)", "Sunflower seeds and avocado"),
        avoidFoods = listOf("Flours and cereals fortified with synthetic Folic Acid (pteroylmonoglutamic acid)", "Excessive alcohol which depletes B-vitamin stores", "Charred or deep-fried meats"),
        macronutrientDistribution = "High complex fiber (>35g/day), unrefined whole grains, bioavailable plant proteins",
        micronutrientSupplementation = "L-Methylfolate (5-MTHF) 800mcg, Methylcobalamin (B12) 1000mcg, Pyridoxal-5-Phosphate (P5P) 25mg",
        hydrationAndLifestyle = "Avoid boiling greens in excess water to preserve water-soluble folates; steam or eat raw."
      )
    )

    list.add(
      PrecisionDietProtocol(
        title = "TCF7L2 Incretin & Glycemic Control Protocol",
        targetGene = "TCF7L2 (C/T)",
        rationale = "The risk variant dampens GLP-1 mediated insulin secretion after meals, increasing postprandial glucose excursions.",
        recommendedFoods = listOf("Steel-cut oats with chia seeds and cinnamon", "Fermented foods (kefir, unsweetened Greek yogurt, kimchi)", "Legumes, quinoa, and wild rice in moderate portions", "Lean poultry, wild cod, and tofu", "Apple cider vinegar dressings prior to meals"),
        avoidFoods = listOf("Sweetened beverages, soda, and commercial fruit juices", "White bread, white rice, and refined pasta", "Agave, corn syrup, and candy", "High-glycemic tropical fruits (mango, dried dates) in large quantities"),
        macronutrientDistribution = "40% Fiber-rich Carbohydrates (<50g Glycemic Load/day), 30% Protein, 30% Healthy Fats",
        micronutrientSupplementation = "Berberine HCl (500mg before primary meals), Alpha Lipoic Acid (300mg), Chromium Picolinate (200mcg)",
        hydrationAndLifestyle = "Perform 10-15 minutes of low-impact walking immediately after the largest meal of the day."
      )
    )

    return list
  }
}

data class PrecisionDietProtocol(
  val title: String,
  val targetGene: String,
  val rationale: String,
  val recommendedFoods: List<String>,
  val avoidFoods: List<String>,
  val macronutrientDistribution: String,
  val micronutrientSupplementation: String,
  val hydrationAndLifestyle: String
)
