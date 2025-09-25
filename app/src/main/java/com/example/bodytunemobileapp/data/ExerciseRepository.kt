package com.example.bodytunemobileapp.data

import com.example.bodytunemobileapp.R

object ExerciseRepository {
    
    fun getExercisesByCategory(category: ExerciseCategory): List<Exercise> {
        return when (category) {
            ExerciseCategory.CARDIO_ENDURANCE -> getCardioExercises()
            ExerciseCategory.STRENGTH_TRAINING -> getStrengthExercises()
            ExerciseCategory.CORE_ABS -> getCoreExercises()
            ExerciseCategory.FLEXIBILITY_MOBILITY -> getFlexibilityExercises()
            ExerciseCategory.HIIT -> getHiitExercises()
            ExerciseCategory.YOGA -> getYogaExercises()
            ExerciseCategory.PILATES -> getPilatesExercises()
        }
    }
    
    private fun getCardioExercises(): List<Exercise> {
        return listOf(
            Exercise(
                id = 1,
                name = "Crunch",
                description = "Full-body warm-up, improves circulation.",
                duration = 15,
                imageResource = R.drawable.crunch,
                category = ExerciseCategory.CARDIO_ENDURANCE,
                instructions = "Crunch is a core exercise that works the abdominal muscles. Lie on your back with knees bent and feet flat, then lift your shoulders and upper back slightly off the floor. Keep your lower back pressed down and engage your abs. It helps strengthen the core and improve ab definition."
            ),
            Exercise(
                id = 2,
                name = "Jumping Jacks",
                description = "Full-body warm-up, improves circulation.",
                duration = 15,
                imageResource = R.drawable.jumping_jack,
                category = ExerciseCategory.CARDIO_ENDURANCE,
                instructions = "Jumping jacks are a full-body cardiovascular exercise. Start with feet together and arms at your sides. Jump while spreading your legs shoulder-width apart and raising your arms overhead. Jump back to starting position. This exercise improves cardiovascular health and coordination."
            ),
            Exercise(
                id = 3,
                name = "Burpees",
                description = "Full-body warm-up, improves circulation.",
                duration = 15,
                imageResource = R.drawable.burpees,
                category = ExerciseCategory.CARDIO_ENDURANCE,
                instructions = "Burpees are a high-intensity full-body exercise. Start standing, drop into a squat, place hands on floor, jump feet back into plank, do a push-up, jump feet back to squat, then jump up with arms overhead."
            ),
            Exercise(
                id = 4,
                name = "Mountain Climbers",
                description = "Full-body warm-up, improves circulation.",
                duration = 15,
                imageResource = R.drawable.mountain_climbers,
                category = ExerciseCategory.CARDIO_ENDURANCE,
                instructions = "Mountain climbers are a dynamic cardio exercise. Start in plank position, then alternate bringing knees toward chest in a running motion while maintaining plank position."
            ),
            Exercise(
                id = 5,
                name = "High Knees",
                description = "Full-body warm-up, improves circulation.",
                duration = 15,
                imageResource = R.drawable.high_knees,
                category = ExerciseCategory.CARDIO_ENDURANCE,
                instructions = "High knees are a cardio exercise that improves leg strength and cardiovascular endurance. Run in place while lifting knees as high as possible toward your chest."
            ),
            Exercise(
                id = 6,
                name = "Running in Place",
                description = "Full-body warm-up, improves circulation.",
                duration = 15,
                imageResource = R.drawable.running_in_place,
                category = ExerciseCategory.CARDIO_ENDURANCE,
                instructions = "Running in place is a simple cardio exercise. Lift your feet alternately while swinging your arms naturally, maintaining a steady rhythm."
            ),
            Exercise(
                id = 7,
                name = "Jump Rope",
                description = "Full-body warm-up, improves circulation.",
                duration = 15,
                imageResource = R.drawable.jump_rope,
                category = ExerciseCategory.CARDIO_ENDURANCE,
                instructions = "Jump rope is an excellent cardiovascular exercise. Hold rope handles, swing rope over head and under feet, jumping with both feet together."
            ),
            Exercise(
                id = 8,
                name = "Step Ups",
                description = "Full-body warm-up, improves circulation.",
                duration = 15,
                imageResource = R.drawable.step_ups,
                category = ExerciseCategory.CARDIO_ENDURANCE,
                instructions = "Step ups target leg muscles and improve cardiovascular fitness. Step up onto a platform with one foot, bring other foot up, then step down in reverse order."
            )
        )
    }
    
    private fun getStrengthExercises(): List<Exercise> {
        return listOf(
            Exercise(
                id = 9,
                name = "Push-ups",
                description = "Upper body strength, chest and arms.",
                duration = 20,
                imageResource = R.drawable.icon,
                category = ExerciseCategory.STRENGTH_TRAINING,
                instructions = "Push-ups strengthen chest, shoulders, and triceps. Start in plank position, lower body until chest nearly touches floor, then push back up."
            ),
            Exercise(
                id = 10,
                name = "Squats",
                description = "Lower body strength, legs and glutes.",
                duration = 20,
                imageResource = R.drawable.icon,
                category = ExerciseCategory.STRENGTH_TRAINING,
                instructions = "Squats target leg and glute muscles. Stand with feet shoulder-width apart, lower body as if sitting back into chair, then return to standing."
            ),
            Exercise(
                id = 11,
                name = "Lunges",
                description = "Lower body strength, legs and glutes.",
                duration = 20,
                imageResource = R.drawable.icon,
                category = ExerciseCategory.STRENGTH_TRAINING,
                instructions = "Lunges strengthen legs and improve balance. Step forward with one leg, lower hips until both knees are bent at 90 degrees, then return to start."
            ),
            Exercise(
                id = 12,
                name = "Planks",
                description = "Core strength and stability.",
                duration = 20,
                imageResource = R.drawable.icon,
                category = ExerciseCategory.STRENGTH_TRAINING,
                instructions = "Planks strengthen core muscles. Hold body in straight line from head to heels, supporting weight on forearms and toes."
            ),
            Exercise(
                id = 13,
                name = "Deadlifts",
                description = "Full body strength, back and legs.",
                duration = 20,
                imageResource = R.drawable.icon,
                category = ExerciseCategory.STRENGTH_TRAINING,
                instructions = "Deadlifts work multiple muscle groups. Stand with feet hip-width apart, bend at hips and knees to lower weights, then return to standing."
            ),
            Exercise(
                id = 14,
                name = "Pull-ups",
                description = "Upper body strength, back and arms.",
                duration = 20,
                imageResource = R.drawable.icon,
                category = ExerciseCategory.STRENGTH_TRAINING,
                instructions = "Pull-ups strengthen back and arm muscles. Hang from bar with arms extended, pull body up until chin clears bar, then lower slowly."
            )
        )
    }
    
    private fun getCoreExercises(): List<Exercise> {
        return listOf(
            Exercise(
                id = 15,
                name = "Crunches",
                description = "Core strengthening, abdominal focus.",
                duration = 15,
                imageResource = R.drawable.crunch,
                category = ExerciseCategory.CORE_ABS,
                instructions = "Crunches target abdominal muscles. Lie on back with knees bent, lift shoulders off ground while contracting abs."
            ),
            Exercise(
                id = 16,
                name = "Bicycle Crunches",
                description = "Core strengthening, obliques focus.",
                duration = 15,
                imageResource = R.drawable.icon,
                category = ExerciseCategory.CORE_ABS,
                instructions = "Bicycle crunches work abs and obliques. Lie on back, bring opposite elbow to knee in cycling motion."
            ),
            Exercise(
                id = 17,
                name = "Russian Twists",
                description = "Core strengthening, obliques focus.",
                duration = 15,
                imageResource = R.drawable.icon,
                category = ExerciseCategory.CORE_ABS,
                instructions = "Russian twists target obliques. Sit with knees bent, lean back slightly, rotate torso side to side."
            ),
            Exercise(
                id = 18,
                name = "Leg Raises",
                description = "Lower ab strengthening.",
                duration = 15,
                imageResource = R.drawable.icon,
                category = ExerciseCategory.CORE_ABS,
                instructions = "Leg raises target lower abs. Lie on back, lift legs straight up while keeping lower back pressed to floor."
            ),
            Exercise(
                id = 19,
                name = "Dead Bug",
                description = "Core stability and coordination.",
                duration = 15,
                imageResource = R.drawable.icon,
                category = ExerciseCategory.CORE_ABS,
                instructions = "Dead bug improves core stability. Lie on back with arms up, alternate extending opposite arm and leg."
            ),
            Exercise(
                id = 20,
                name = "Side Planks",
                description = "Oblique strengthening.",
                duration = 15,
                imageResource = R.drawable.icon,
                category = ExerciseCategory.CORE_ABS,
                instructions = "Side planks target obliques. Lie on side, prop up on forearm, lift hips to create straight line."
            ),
            Exercise(
                id = 21,
                name = "Hollow Body Hold",
                description = "Full core strengthening.",
                duration = 15,
                imageResource = R.drawable.icon,
                category = ExerciseCategory.CORE_ABS,
                instructions = "Hollow body hold strengthens entire core. Lie on back, lift shoulders and legs off ground, hold position."
            )
        )
    }
    
    private fun getFlexibilityExercises(): List<Exercise> {
        return listOf(
            Exercise(
                id = 22,
                name = "Forward Fold",
                description = "Hamstring and back stretch.",
                duration = 10,
                imageResource = R.drawable.icon,
                category = ExerciseCategory.FLEXIBILITY_MOBILITY,
                instructions = "Forward fold stretches hamstrings and back. Stand with feet hip-width apart, hinge at hips to fold forward."
            ),
            Exercise(
                id = 23,
                name = "Cat-Cow Stretch",
                description = "Spine mobility and flexibility.",
                duration = 10,
                imageResource = R.drawable.icon,
                category = ExerciseCategory.FLEXIBILITY_MOBILITY,
                instructions = "Cat-cow improves spine mobility. Start on hands and knees, alternate arching and rounding back."
            ),
            Exercise(
                id = 24,
                name = "Hip Circles",
                description = "Hip mobility and flexibility.",
                duration = 10,
                imageResource = R.drawable.icon,
                category = ExerciseCategory.FLEXIBILITY_MOBILITY,
                instructions = "Hip circles improve hip mobility. Stand with hands on hips, make large circles with hips."
            ),
            Exercise(
                id = 25,
                name = "Shoulder Rolls",
                description = "Shoulder mobility and tension relief.",
                duration = 10,
                imageResource = R.drawable.icon,
                category = ExerciseCategory.FLEXIBILITY_MOBILITY,
                instructions = "Shoulder rolls relieve tension. Roll shoulders forward and backward in circular motion."
            ),
            Exercise(
                id = 26,
                name = "Neck Stretches",
                description = "Neck flexibility and tension relief.",
                duration = 10,
                imageResource = R.drawable.icon,
                category = ExerciseCategory.FLEXIBILITY_MOBILITY,
                instructions = "Neck stretches relieve tension. Gently tilt head side to side and forward and back."
            ),
            Exercise(
                id = 27,
                name = "Quad Stretch",
                description = "Quadriceps flexibility.",
                duration = 10,
                imageResource = R.drawable.icon,
                category = ExerciseCategory.FLEXIBILITY_MOBILITY,
                instructions = "Quad stretch targets front thigh muscles. Stand on one leg, pull other foot toward glutes."
            ),
            Exercise(
                id = 28,
                name = "Calf Stretch",
                description = "Calf muscle flexibility.",
                duration = 10,
                imageResource = R.drawable.icon,
                category = ExerciseCategory.FLEXIBILITY_MOBILITY,
                instructions = "Calf stretch targets lower leg muscles. Step back with one leg, keep heel down, lean forward."
            ),
            Exercise(
                id = 29,
                name = "Chest Stretch",
                description = "Chest and shoulder flexibility.",
                duration = 10,
                imageResource = R.drawable.icon,
                category = ExerciseCategory.FLEXIBILITY_MOBILITY,
                instructions = "Chest stretch opens chest muscles. Place arm against wall, turn body away from arm."
            )
        )
    }
    
    private fun getHiitExercises(): List<Exercise> {
        return listOf(
            Exercise(
                id = 30,
                name = "HIIT Burpees",
                description = "High-intensity full-body exercise.",
                duration = 25,
                imageResource = R.drawable.icon,
                category = ExerciseCategory.HIIT,
                instructions = "High-intensity burpees for maximum calorie burn. Perform burpees at maximum intensity for set intervals."
            ),
            Exercise(
                id = 31,
                name = "Sprint Intervals",
                description = "High-intensity cardio intervals.",
                duration = 25,
                imageResource = R.drawable.icon,
                category = ExerciseCategory.HIIT,
                instructions = "Sprint intervals improve cardiovascular fitness. Alternate between high-intensity sprints and recovery periods."
            ),
            Exercise(
                id = 32,
                name = "Tabata Squats",
                description = "High-intensity leg workout.",
                duration = 25,
                imageResource = R.drawable.icon,
                category = ExerciseCategory.HIIT,
                instructions = "Tabata squats for leg strength and endurance. Perform squats at maximum intensity for 20 seconds, rest 10 seconds."
            ),
            Exercise(
                id = 33,
                name = "HIIT Mountain Climbers",
                description = "High-intensity core and cardio.",
                duration = 25,
                imageResource = R.drawable.icon,
                category = ExerciseCategory.HIIT,
                instructions = "High-intensity mountain climbers for core and cardio. Perform at maximum speed for set intervals."
            ),
            Exercise(
                id = 34,
                name = "Jump Squat Intervals",
                description = "High-intensity plyometric exercise.",
                duration = 25,
                imageResource = R.drawable.icon,
                category = ExerciseCategory.HIIT,
                instructions = "Jump squat intervals for explosive power. Perform jump squats at high intensity with rest periods."
            )
        )
    }
    
    private fun getYogaExercises(): List<Exercise> {
        return listOf(
            Exercise(
                id = 35,
                name = "Downward Dog",
                description = "Full body stretch and strengthening.",
                duration = 12,
                imageResource = R.drawable.icon,
                category = ExerciseCategory.YOGA,
                instructions = "Downward dog stretches and strengthens. Start on hands and knees, lift hips up and back."
            ),
            Exercise(
                id = 36,
                name = "Warrior I",
                description = "Leg strengthening and hip opening.",
                duration = 12,
                imageResource = R.drawable.icon,
                category = ExerciseCategory.YOGA,
                instructions = "Warrior I strengthens legs and opens hips. Step back into lunge, raise arms overhead."
            ),
            Exercise(
                id = 37,
                name = "Tree Pose",
                description = "Balance and core strengthening.",
                duration = 12,
                imageResource = R.drawable.icon,
                category = ExerciseCategory.YOGA,
                instructions = "Tree pose improves balance. Stand on one leg, place other foot on inner thigh, hands in prayer."
            ),
            Exercise(
                id = 38,
                name = "Child's Pose",
                description = "Relaxation and gentle stretch.",
                duration = 12,
                imageResource = R.drawable.icon,
                category = ExerciseCategory.YOGA,
                instructions = "Child's pose provides relaxation. Kneel, sit back on heels, fold forward with arms extended."
            ),
            Exercise(
                id = 39,
                name = "Cobra Pose",
                description = "Back strengthening and chest opening.",
                duration = 12,
                imageResource = R.drawable.icon,
                category = ExerciseCategory.YOGA,
                instructions = "Cobra pose strengthens back and opens chest. Lie face down, press hands to lift chest."
            ),
            Exercise(
                id = 40,
                name = "Triangle Pose",
                description = "Side body stretch and strengthening.",
                duration = 12,
                imageResource = R.drawable.icon,
                category = ExerciseCategory.YOGA,
                instructions = "Triangle pose stretches side body. Stand wide, reach one hand to floor, other to sky."
            ),
            Exercise(
                id = 41,
                name = "Bridge Pose",
                description = "Back strengthening and hip opening.",
                duration = 12,
                imageResource = R.drawable.icon,
                category = ExerciseCategory.YOGA,
                instructions = "Bridge pose strengthens back and opens hips. Lie on back, lift hips up while feet stay grounded."
            ),
            Exercise(
                id = 42,
                name = "Seated Twist",
                description = "Spine mobility and detoxification.",
                duration = 12,
                imageResource = R.drawable.icon,
                category = ExerciseCategory.YOGA,
                instructions = "Seated twist improves spine mobility. Sit cross-legged, twist torso to one side, then other."
            ),
            Exercise(
                id = 43,
                name = "Savasana",
                description = "Deep relaxation and meditation.",
                duration = 12,
                imageResource = R.drawable.icon,
                category = ExerciseCategory.YOGA,
                instructions = "Savasana provides deep relaxation. Lie flat on back, arms at sides, focus on breathing."
            )
        )
    }
    
    private fun getPilatesExercises(): List<Exercise> {
        return listOf(
            Exercise(
                id = 44,
                name = "Pilates Roll-Up",
                description = "Core strengthening and spine mobility.",
                duration = 18,
                imageResource = R.drawable.icon,
                category = ExerciseCategory.PILATES,
                instructions = "Pilates roll-up strengthens core. Lie on back, slowly roll up to sitting, then back down."
            ),
            Exercise(
                id = 45,
                name = "The Hundred",
                description = "Core strengthening and breathing.",
                duration = 15,
                imageResource = R.drawable.icon,
                category = ExerciseCategory.PILATES,
                instructions = "The Hundred improves core strength and breathing. Lie on back, pump arms while holding legs up."
            ),
            Exercise(
                id = 46,
                name = "Leg Circles",
                description = "Hip mobility and core stability.",
                duration = 15,
                imageResource = R.drawable.icon,
                category = ExerciseCategory.PILATES,
                instructions = "Leg circles improve hip mobility. Lie on back, make circles with one leg while keeping core stable."
            ),
            Exercise(
                id = 47,
                name = "Pilates Teaser",
                description = "Advanced core strengthening.",
                duration = 15,
                imageResource = R.drawable.icon,
                category = ExerciseCategory.PILATES,
                instructions = "Pilates teaser challenges core strength. Balance on tailbone with legs and arms extended."
            ),
            Exercise(
                id = 48,
                name = "Swimming",
                description = "Back strengthening and coordination.",
                duration = 15,
                imageResource = R.drawable.icon,
                category = ExerciseCategory.PILATES,
                instructions = "Swimming strengthens back muscles. Lie face down, lift opposite arm and leg alternately."
            ),
            Exercise(
                id = 49,
                name = "The Saw",
                description = "Spine rotation and flexibility.",
                duration = 15,
                imageResource = R.drawable.icon,
                category = ExerciseCategory.PILATES,
                instructions = "Pilates saw improves spine rotation. Sit with legs wide, twist and reach opposite hand to foot."
            )
        )
    }
}
