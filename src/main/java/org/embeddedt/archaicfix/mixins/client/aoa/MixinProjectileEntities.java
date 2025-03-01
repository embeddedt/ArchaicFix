package org.embeddedt.archaicfix.mixins.client.aoa;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

// To generate mixin targets, deobfuscate and decompile Nevermine-Tslat-1.1.2.jar
// (sha256: 219FC2B13FA60658CC2D73BF4587D1570D4954A02EC7A209E6983F32DC307801), and run this python script.
// (Tip: delete net/nevermine/structures before decompiling to avoid an OutOfMemoryError)

// from pathlib import Path
// import re
// 
// pattern = re.compile(r"@SideOnly\(Side\.CLIENT\)\s+public void onEntityUpdate\(\)")
// classes = []
// 
// for path in Path(".").glob("**/*.java"):
//     text = open(path, "r", encoding="utf8").read();
//     
//     if pattern.search(text):
//         classes.append(".".join(path.with_suffix("").parts))
// 
// print(", ".join(['"' + x + '"' for x in classes]))

@Pseudo
@Mixin(targets = {"net.nevermine.projectiles.cannon.EntityAncientDischarger", "net.nevermine.projectiles.cannon.EntityBoomBall", "net.nevermine.projectiles.cannon.EntityBoomCannonGrenade", "net.nevermine.projectiles.cannon.EntityEnergyShot", "net.nevermine.projectiles.cannon.EntityFloroRPG", "net.nevermine.projectiles.cannon.EntityFungalBall", "net.nevermine.projectiles.cannon.EntityGoldenCannonball", "net.nevermine.projectiles.cannon.EntityMoonCannonShot", "net.nevermine.projectiles.cannon.EntityReturningGrenade", "net.nevermine.projectiles.cannon.EntityRockFragmentFungal", "net.nevermine.projectiles.cannon.EntityRPGShot", "net.nevermine.projectiles.cannon.EntityWaterBalloonBomb", "net.nevermine.projectiles.enemy.EntityBamShot", "net.nevermine.projectiles.enemy.EntityBaronessShot", "net.nevermine.projectiles.enemy.EntityBloodball", "net.nevermine.projectiles.enemy.EntityClownShot", "net.nevermine.projectiles.enemy.EntityClunkheadShot", "net.nevermine.projectiles.enemy.EntityConstructShot", "net.nevermine.projectiles.enemy.EntityCraexxeusNuke", "net.nevermine.projectiles.enemy.EntityCraexxeusShot", "net.nevermine.projectiles.enemy.EntityCreeperShot", "net.nevermine.projectiles.enemy.EntityCreepTube", "net.nevermine.projectiles.enemy.EntityDestructorShot", "net.nevermine.projectiles.enemy.EntityFungiShot", "net.nevermine.projectiles.enemy.EntityGuardianProjectileBlue", "net.nevermine.projectiles.enemy.EntityGuardianProjectileGreen", "net.nevermine.projectiles.enemy.EntityGuardianProjectileRed", "net.nevermine.projectiles.enemy.EntityGuardianProjectileYellow", "net.nevermine.projectiles.enemy.EntityHagShot", "net.nevermine.projectiles.enemy.EntityLelyetianShot", "net.nevermine.projectiles.enemy.EntityMagicBall", "net.nevermine.projectiles.enemy.EntityMagicBallSeaTroll", "net.nevermine.projectiles.enemy.EntityMagicBallWither", "net.nevermine.projectiles.enemy.EntityMechShot", "net.nevermine.projectiles.enemy.EntityMiskelShot", "net.nevermine.projectiles.enemy.EntityPenumbraShot", "net.nevermine.projectiles.enemy.EntityReaperShot", "net.nevermine.projectiles.enemy.EntityRunicGuardianShot", "net.nevermine.projectiles.enemy.EntitySkelemanShot", "net.nevermine.projectiles.enemy.EntitySpectralShot", "net.nevermine.projectiles.enemy.EntitySurgeBlue", "net.nevermine.projectiles.enemy.EntitySurgeRed", "net.nevermine.projectiles.energy.EntityBloodDrainShot", "net.nevermine.projectiles.energy.EntityConfetti", "net.nevermine.projectiles.energy.EntityDeathRayShot", "net.nevermine.projectiles.energy.EntityDestroyerShot", "net.nevermine.projectiles.energy.EntityDoomShot", "net.nevermine.projectiles.energy.EntityGoldBringer", "net.nevermine.projectiles.energy.EntityIceBlastShot", "net.nevermine.projectiles.energy.EntityIonShot", "net.nevermine.projectiles.energy.EntityIroMineShot", "net.nevermine.projectiles.energy.EntityLightBlaster", "net.nevermine.projectiles.energy.EntityLightSpark", "net.nevermine.projectiles.energy.EntityMindShot", "net.nevermine.projectiles.energy.EntityMoonDestroyer", "net.nevermine.projectiles.energy.EntityMoonShineShot", "net.nevermine.projectiles.energy.EntityOdiousBeam", "net.nevermine.projectiles.energy.EntityParalyzerShot", "net.nevermine.projectiles.energy.EntityPowerRayShot", "net.nevermine.projectiles.energy.EntityReefShot", "net.nevermine.projectiles.energy.EntityShyreShot", "net.nevermine.projectiles.energy.EntitySoulSparkShot", "net.nevermine.projectiles.energy.EntitySwarmShot", "net.nevermine.projectiles.energy.EntityToxicShot", "net.nevermine.projectiles.energy.EntityVortex", "net.nevermine.projectiles.energy.EntityWinder", "net.nevermine.projectiles.energy.EntityWitherPierce", "net.nevermine.projectiles.gun.EntityFloroMetalPellet", "net.nevermine.projectiles.gun.EntityGoldenBullet", "net.nevermine.projectiles.gun.EntityIominatorShot", "net.nevermine.projectiles.gun.EntityLightSlug", "net.nevermine.projectiles.gun.EntityMetalFungShot", "net.nevermine.projectiles.gun.EntityMetalPelletFire", "net.nevermine.projectiles.gun.EntityMoonMaker", "net.nevermine.projectiles.gun.EntitySkeletalPellet", "net.nevermine.projectiles.gun.EntitySpineShot", "net.nevermine.projectiles.gun.EntitySwarmShot", "net.nevermine.projectiles.gun.EntityTigerShot", "net.nevermine.projectiles.staff.EntityFireflyStaff", "net.nevermine.projectiles.staff.EntityHauntersShot", "net.nevermine.projectiles.staff.EntityLyonicShot", "net.nevermine.projectiles.staff.EntityPolymorphShot", "net.nevermine.projectiles.staff.EntityPrimordialShot", "net.nevermine.projectiles.staff.EntityRosidianShot", "net.nevermine.projectiles.staff.EntityStaffAquatic", "net.nevermine.projectiles.staff.EntityStaffBaron", "net.nevermine.projectiles.staff.EntityStaffFire", "net.nevermine.projectiles.staff.EntityStaffGhoul", "net.nevermine.projectiles.staff.EntityStaffMecha", "net.nevermine.projectiles.staff.EntityStaffPoison", "net.nevermine.projectiles.staff.EntityStaffPower", "net.nevermine.projectiles.staff.EntityStaffWater", "net.nevermine.projectiles.staff.EntityStaffWind", "net.nevermine.projectiles.staff.EntityStaffWither", "net.nevermine.projectiles.staff.EntitySunEmitter", "net.nevermine.projectiles.staff.EntitySunShot", "net.nevermine.projectiles.sticky.EntityBlastBomb", "net.nevermine.projectiles.sticky.EntityErebonShot", "net.nevermine.projectiles.sticky.EntityErebonStickler", "net.nevermine.projectiles.sticky.EntityLuxonShot", "net.nevermine.projectiles.sticky.EntityLuxonStickler", "net.nevermine.projectiles.sticky.EntityPlutonShot", "net.nevermine.projectiles.sticky.EntityPlutonStickler", "net.nevermine.projectiles.sticky.EntitySelyanShot", "net.nevermine.projectiles.sticky.EntitySelyanStickler", "net.nevermine.projectiles.sticky.EntityStickyBomb", "net.nevermine.projectiles.throwable.EntityHellFireProjectile"})
public abstract class MixinProjectileEntities extends EntityThrowable {
    public MixinProjectileEntities(World worldIn) {
        super(worldIn);
        throw new RuntimeException();
    }

    /**
     * @reason AoA spawns particles on the server thread, causing re-entrance issues. Let's make it not do that.
     */
    // Mixin is unwilling to generate a refmap for the target method, so we're using a wildcard.
    @WrapWithCondition(method = "*()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/EffectRenderer;addEffect(Lnet/minecraft/client/particle/EntityFX;)V"))
    public boolean onlySpawnParticlesOnClientThread(EffectRenderer efr, EntityFX entity) {
        return this.worldObj.isRemote;
    }
}
